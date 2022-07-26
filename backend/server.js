const express = require("express");
const app = express();
const http = require("http");
const server = http.createServer(app);
const { Server } = require("socket.io");

app.use(express.json());

app.use(express.urlencoded({ extended: false }));

const io = new Server(server);

var sockets = [];

app.post("/sendlocation", (req, res) => {
  const id = req.headers.uuid;
  console.log(
    Date(),
    `LOC UPDATE :: ${id} -> LAT:${req.body.lat} LNG:${req.body.lng}`
  );

  if (sockets[id])
    sockets[id].sockets.map((socket) => [
      socket.emit("chat message", req.body),
    ]);
  res.sendStatus(200);
});

io.of("/").on("connection", (socket) => {
  var id = socket.handshake.query.uuid;

  console.log(Date(), `TRACKING   :: ${id}`);

  if (!id || !sockets[id]) {
    sockets[id] = {
      id,
      sockets: [socket],
    };
  } else sockets[id].sockets.push(socket);

  socket.on("disconnect", (e) => {
    sockets[socket.handshake.query.uuid].sockets = sockets[
      socket.handshake.query.uuid
    ].sockets.filter((s) => s.id !== socket.id);

    console.log(
      Date(),
      `DISCONNECT :: ${sockets[socket.handshake.query.uuid].id}`
    );

    if (sockets[socket.handshake.query.uuid].sockets.length === 0) {
      console.log(
        Date(),
        `NO TRACKER :: ${sockets[socket.handshake.query.uuid].id}`
      );

      sockets = sockets.filter(
        (socket) => socket.id !== socket.handshake.query.uuid
      );
    }
  });
});

server.listen(process.env.PORT || 4000, () => {
  console.log(Date(), "listening on *:4000");
});
