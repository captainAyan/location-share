const express = require("express");
const http = require("http");

const app = express();
const server = http.createServer(app);
const { Server } = require("socket.io");

app.use(express.json());

app.use(express.urlencoded({ extended: false }));

const io = new Server(server);

let trackees = [];

app.post("/sendlocation", (req, res) => {
  const id = req.headers.uuid;
  console.log(
    Date(),
    `LOC UPDATE :: ${id} -> LAT:${req.body.lat} LNG:${req.body.lng}`
  );

  if (trackees[id])
    trackees[id].trackers.map((socket) => [
      socket.emit("chat message", {
        ...req.body,
        total_trackers: trackees[id].trackers.length,
      }),
    ]);
  res.status(200).send({ total_trackers: trackees[id]?.trackers.length });
});

io.of("/").on("connection", (socket) => {
  const { uuid } = socket.handshake.query;

  console.log(Date(), `TRACKING   :: ${uuid}`);

  if (!uuid || !trackees[uuid]) {
    trackees[uuid] = {
      id: uuid,
      trackers: [socket],
    };
  } else trackees[uuid].trackers.push(socket);

  socket.on("disconnect", () => {
    trackees[uuid].trackers = trackees[uuid].trackers.filter(
      (s) => s.id !== socket.id
    );

    console.log(Date(), `DISCONNECT :: ${uuid}`);

    if (trackees[uuid].trackers.length === 0) {
      console.log(Date(), `NO TRACKER :: ${trackees[uuid].id}`);

      trackees = trackees.filter((trackee) => trackee.id !== uuid);
    }
  });
});

server.listen(process.env.PORT || 4000, () => {
  console.log(Date(), "listening on *:4000");
});
