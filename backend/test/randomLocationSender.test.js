/**
 * This test lets you generate locations and sends them to the server. The
 * location are generated in such a way that the every location will be within
 * a reasonable distance away from the previous location
 */

const axios = require("axios").default;
require("dotenv").config();

const UUID = process.env.UUID;

const BASE_URL = process.env.BASE_URL;
const LOCATION_SENDER_ROUTE = "/sendlocation";

const MIN_ANGLE = 45;
const MAX_ANGLE = 135;

let [lat, lng] = [22.6535307, 88.3795288]; // initial

function getNewLocation(lat, lng) {
  let degree = Math.floor(
    Math.random() * (MAX_ANGLE - MIN_ANGLE + 1) + MIN_ANGLE
  );

  let radian = (degree * Math.PI) / 180;

  new_lat = lat + 0.0004 * Math.sin(radian);
  new_lng = lng + 0.0004 * Math.cos(radian);

  return [new_lat, new_lng];
}

setInterval(() => {
  axios.post(
    BASE_URL + LOCATION_SENDER_ROUTE,
    { lat, lng },
    { headers: { uuid: UUID } }
  );

  console.log("SENT ", lat, lng);

  [lat, lng] = getNewLocation(lat, lng);
}, 5000);
