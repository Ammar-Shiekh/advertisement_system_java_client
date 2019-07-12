# Advertisements system java client

A java client for [Advertisements system](https://github.com/Ammar-Shiekh/advertisement_system) that uses [Pusher](https://pusher.com/) for broadcasting.

This client program should run on a screen device. 

## Features

1. Display the provided advertisements as a queue.
2. Listen to server events for Adding/Updating/Removing advertisements, and process accordingly.

This client should be configured with the device and server information first to successfully run. If it's not configured it'll show a wizard to enter these configurations on startup.

## Setup

Go to `advertisements.ServerAdapter` and set your `PUSHER_APP_KEY` and `PUSHER_CLUSTER`.
