# Inventory Agent for Android

![Flyve MDM banner](https://user-images.githubusercontent.com/663460/26935464-54267e9c-4c6c-11e7-86df-8cfa6658133e.png)

[![License](https://img.shields.io/github/license/flyve-mdm/android-inventory-agent.svg?&label=License)](https://github.com/flyve-mdm/android-inventory-agent/blob/master/LICENSE.md)
[![Follow twitter](https://img.shields.io/twitter/follow/FlyveMDM.svg?style=social&label=Twitter&style=flat-square)](https://twitter.com/FlyveMDM)
[![Telegram Group](https://img.shields.io/badge/Telegram-Group-blue.svg)](https://t.me/flyvemdm)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://conventionalcommits.org)
[![Greenkeeper badge](https://badges.greenkeeper.io/flyve-mdm/android-inventory-agent.svg)](https://greenkeeper.io/)
[![GitHub release](https://img.shields.io/github/release/flyve-mdm/android-inventory-agent.svg)](https://github.com/flyve-mdm/android-inventory-agent/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.flyvemdm/inventory.svg)](https://bintray.com/flyve-mdm/inventory/android-inventory-agent/)

Flyve MDM is a Mobile device management software that enables you to secure and manage all the mobile devices of your business or family via a web-based console.

To get started, check out [Flyve MDM Website](https://flyve-mdm.com/)!

## Table of contents

* [Synopsis](#synopsis)
* [Build Status](#build-status)
* [Compatibility Matrix](#compatibility-matrix)
* [Installation](#installation)
* [Documentation](#documentation)
* [Versioning](#versioning)
* [Contribute](#contribute)
* [Contact](#contact)
* [Copying](#copying)

## Synopsis

This application is the Android inventory agent of the Inventory project.

It features a complete inventory of your Android devices: both hardware and software informations are collected. You get the data about processor, memory, drives, sensors, the list and description of installed application (apk) and [more](#data-collected).

Inventory Agent for Android is running on Android 1.6 and higher.

Inventory Agent for Android can send inventory to:
- Inventory for GLPI 2.3.x and higher
- OCSInventory NG (ocsng) 1.3.x and 2.x
- Mandriva Pulse2

The Inventory project is a free software project providing:
- hardware and software inventory (multiplatform)
- network discovery
- network inventory for printers and switches
- Wake On Lan (WOL)
- Software deployment
- total integration with the GLPI project (open source asset management software and helpdesk)

Inventory agents can also be used with other open sources projects like Uranos or Rudder.

#### Data collected:

- USB
- Hardware
- Sensors
- Software
- Memories
- Cameras
- Networks
- Battery
- CPUs
- BIOS
- Inputs
- Drives
- Accesslog
- SIM Cards
- Environments variables
- JVM
- Videos

Visit our [website](http://flyve.org/android-inventory-agent/) for more information.

## Build Status

| **Release channel** | **Beta channel** |
|:---:|:---:|
| [![Build Status](https://circleci.com/gh/flyve-mdm/android-inventory-agent/tree/master.svg?style=svg)](https://circleci.com/gh/flyve-mdm/android-inventory-agent/tree/master) | [![Build Status](https://circleci.com/gh/flyve-mdm/android-inventory-agent/tree/develop.svg?style=svg)](https://circleci.com/gh/flyve-mdm/android-inventory-agent/tree/develop) |

## Compatibility Matrix

|GLPI|9.1.1|9.1.2|9.1.3|9.1.4|9.1.5|9.1.6|9.2.0|
|:---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
|**Flyve MDM**||||1.0|||||
|**FusionInventory**||||9.1+1.1|||||

## Installation

[<img src="https://user-images.githubusercontent.com/663460/26973322-4ddf78a4-4d16-11e7-8b58-4c03b4bc2490.png" alt="Get it on Google Play" height="60">](https://play.google.com/store/apps/details?id=org.flyve.inventory.agent) [<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="60">](https://f-droid.org/app/org.flyve.inventory.agent) [<img src="https://user-images.githubusercontent.com/663460/26973090-f8fdc986-4d14-11e7-995a-e7c5e79ed925.png" alt="Download APK from GitHub" height="60">](https://github.com/flyve-mdm/android-inventory-agent/releases/latest)

## Documentation

We maintain a detailed documentation of the project on its [website](http://flyve.org/android-inventory-agent/).

## Versioning

In order to provide transparency on our release cycle and to maintain backward compatibility, Flyve MDM is maintained under [the Semantic Versioning guidelines](http://semver.org/). We are committed to following and complying with the rules, the best we can.

See [the tags section of our GitHub project](http://github.com/flyve-mdm/android-inventory-agent/tags) for changelogs for each release version of Flyve MDM. Release announcement posts on [the official Teclib' blog](http://www.teclib-edition.com/en/communities/blog-posts/) contain summaries of the most noteworthy changes made in each release.

## Contribute

Want to file a bug, contribute some code, or improve documentation? Excellent! Read up on our
guidelines for [contributing](./CONTRIBUTING.md) and then check out one of our issues in the [Issues Dashboard](https://github.com/flyve-mdm/android-inventory-agent/issues).

## Contact

For notices about major changes and general discussion of Flyve MDM development, subscribe to the [/r/FlyveMDM](http://www.reddit.com/r/FlyveMDM) subreddit.
You can also chat with us via IRC in [#flyve-mdm on freenode](http://webchat.freenode.net/?channels=flyve-mdm]).
Ping me @rafaelje in the IRC chatroom if you get stuck.

## Copying

* **Name**: [Flyve MDM](https://flyve-mdm.com/) is a registered trademark of [Teclib'](http://www.teclib-edition.com/en/).
* **Code**: you can redistribute it and/or modify
    it under the terms of the GNU General Public License ([GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html)).
* **Documentation**: released under Attribution 4.0 International ([CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)).
