# SignedVelocity

![Latest Version](https://img.shields.io/github/v/release/4drian3d/SignedVelocity?style=flat-square)
[![Discord](https://img.shields.io/discord/899740810956910683?color=7289da&logo=Discord&label=Discord&style=flat-square)](https://discord.gg/5NMMzK5mAn)
![Modrinth Downloads](https://img.shields.io/modrinth/dt/7IbzD4Zm?logo=Modrinth&style=flat-square)

Allows you to cancel or modify messages or commands from Velocity without synchronization problems

## Requirements
- Velocity 3.2.0+
- Java 17+
- Paper 1.19.4+ or Sponge 1.16.5+ (API 8.1+)

## Installation
In order for SignedVelocity to work, you must install it on both Velocity and all your servers
### Velocity
- Download SignedVelocity-Proxy
- Drag and drop on your Velocity plugins folder
- Start your proxy
### Paper
- Download SignedVelocity-Paper
- Drag and drop on your Paper plugins folder
- Start your server
### Sponge
- Download the version of SignedVelocity-Sponge that is compatible with your server.
  SignedVelocity-Sponge-8 supports API 8.1 and 9, SignedVelocity-Sponge-10 supports API 10 and 11
- Drag and drop on your Sponge plugins folder
- Start your server

## Downloads

[![](https://raw.githubusercontent.com/Prospector/badges/master/modrinth-badge-72h-padded.png)](https://modrinth.com/plugin/signedvelocity)

### Why SignedVelocity if UnSignedVelocity already exists?

UnSignedVelocity is a plugin that removes the limitation in Velocity to block or modify chat or commands executed by players with versions 1.19.1 or higher and who have a valid SignedKey. Although this works most of the time, there are issues that have arisen since version 1.19.3, which, as Mojang has implemented more security checks in the chat reporting system, has driven players kicked out of the proxy.

SignedVelocity solves all these problems, synchronizing the blocking and modification of chat and commands to the backend server, where it is possible to modify the chat.

[![Watch the video](https://img.youtube.com/vi/5bfYy1kQwGk/maxresdefault.jpg)](https://www.youtube.com/watch?v=5bfYy1kQwGk)

## Metrics
[![metrics](https://bstats.org/signatures/velocity/SignedVelocity.svg)](https://bstats.org/plugin/velocity/SignedVelocity/18937)