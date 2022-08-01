/**
 * This test lets you generate locations and sends them to the server. The
 * location are generated in such a way that the every location will be within
 * a reasonable distance away from the previous location
 */

const axios = require("axios").default;
require("dotenv").config();

const { UUID, BASE_URL, LOCAL_BASE_URL, NODE_ENV } = process.env;
const LOCATION_SENDER_ROUTE = "/sendlocation";

const MIN_ANGLE = 45;
const MAX_ANGLE = 135;

let index = 0;

let [lat, lng] = [22.6535307, 88.3795288]; // initial

function getNewLocation(_lat, _lng) {
  const degree = Math.floor(
    Math.random() * (MAX_ANGLE - MIN_ANGLE + 1) + MIN_ANGLE
  );

  const radian = (degree * Math.PI) / 180;

  const newLat = _lat + 0.0004 * Math.sin(radian);
  const newLng = _lng + 0.0004 * Math.cos(radian);

  return [newLat, newLng];
}

setInterval(async () => {
  console.log(index, "SENT ", lat, lng);
  try {
    await axios.post(
      (NODE_ENV === "production" ? BASE_URL : LOCAL_BASE_URL) +
        LOCATION_SENDER_ROUTE,
      { lat, lng },
      { headers: { uuid: UUID } }
    );
  } catch (e) {
    console.log(index, e.message);
  }
  index += 1;

  [lat, lng] = getNewLocation(lat, lng);
}, 5000);
