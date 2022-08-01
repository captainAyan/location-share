<p align="center">
  <img src="https://github.com/captainayan/location-share/blob/main/logo.png?raw=true" width="96px"/>
  <h1 align="center">Location Share 🗺️</h1>
  <p align="center">
    An app that lets you share your location with your friends.
    <br />
    <a href="https://github.com/captainAyan/location-share/releases/latest">Download</a>
    ·
    <a href="https://github.com/captainAyan/location-share/issues">Report Bug 😓</a>
  </p>
</p>

## About
Location Share is an Android app that lets you share your location with your friends anonymously. It require not login or signup to use the app. Friends are added using QR Code and save in the user's device locally.

## Getting Started
🚧 Work in progress

### Self-Hosted (Server)
- Clone the repo
- Create a Heroku project
- Add the heroku remote
- Since, the server is in the `backend` folder, there ae some additional settings required
    - Add `https://github.com/timanovsky/subdir-heroku-buildpack.git` as the first *buildpack* under `Dashboard > Settings > Buildpacks`
    - Add a `config variable` (under *Config Vars*) namely `PROJECT_PATH` with the value `backend`
    - Finally add another *buildpack*, namely `heroku/nodejs`
- Now push the `main` branch to the `heroku` remote

## Demo
🚧 Work in progress

## Poster
![App Poster](https://github.com/captainayan/location-share/blob/main/poster.png?raw=true)

## Contribution
Just send me a pull request. Mention your discord or instagram id.

(if the instructions were unclear, please let me know)

## Contact
Send me a message on discord or instagram. Check out my [Profile Readme](https://github.com/captainAyan)
