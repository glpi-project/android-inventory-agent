---
layout: default
---

## Synopsis

This library help you to create a complete inventory of your iOS devices: both hardware and software informations are collected. You get the data about processor, memory, drives, sensors, etc. and also the list and description of installed application on any devices in a beautifull XML as protocol compatible with FusionInventory for GLPI.

You can find more information here:
<http://fusioninventory.org/documentation/dev/spec/protocol/inventory.html>

**What's included?**

* Hardware
* Bios
* Memory
* Inputs
* Sensors
* Drives
* Cpus
* Simcards
* Videos
* Cameras
* Networks
* Envs
* Jvm
* Softwares
* Usb
* Battery

## Build Status

Build with Xcode 8.3.2 / Swift 3.1

| **Release channel** | **Beta channel** |
|:---:|:---:|
| [![Build Status](https://travis-ci.org/flyve-mdm/flyve-mdm-ios-inventory.svg?branch=master)](https://travis-ci.org/flyve-mdm/flyve-mdm-ios-inventory) | [![Build Status](https://travis-ci.org/flyve-mdm/flyve-mdm-ios-inventory.svg?branch=develop)](https://travis-ci.org/flyve-mdm/flyve-mdm-ios-inventory) |

## Installation

### CocoaPods

> Less Hassle, More OSS

<https://cocoapods.org/pods/FlyveMDMInventory>

Install using [CocoaPods](http://cocoapods.org) by adding this line to your Podfile:

````ruby
use_frameworks! # Add this if you are targeting iOS 8+ or using Swift
pod 'FlyveMDMInventory'
````

Then, run the following command:

```console
pod install
```

### Carthage

Install using [Carthage](https://github.com/Carthage/Carthage) by adding the following lines to your Cartfile:

```console
github "flyve-mdm/flyve-mdm-ios-inventory" "master"
```

Then, run the following command:

```console
carthage update --platform iOS
```

* On your application targets “General” settings tab, in the “Linked Frameworks and Libraries” section, drag and drop each framework you want to use from the Carthage/Build folder on disk.

* On your application targets “Build Phases” settings tab, click the “+” icon and choose “New Run Script Phase”. Create a Run Script with the following contents:

```console
/usr/local/bin/carthage copy-frameworks
```

* And add the paths to the frameworks you want to use under “Input Files”, e.g.:

```console
$(SRCROOT)/Carthage/Build/iOS/FlyveMDMInventory.framework

```

## Code Example

It's easy to implement in your code

```swift
import FlyveMDMInventory

let inventoryTask = InventoryTask()
inventoryTask.execute("Agent_v1.0", tag:"1.0") { result in
    print(result)
}

```

** _Tag is optional_

## Documentation

We share long-form content about the project in the [wiki](https://github.com/flyve-mdm/flyve-mdm-ios-inventory/wiki).

## Contact

For notices about major changes and general discussion of Flyve MDM development, subscribe to the [/r/FlyveMDM](http://www.reddit.com/r/FlyveMDM) subreddit.
You can also chat with us via IRC in [#flyve-mdm on freenode](http://webchat.freenode.net/?channels=flyve-mdm]).
Ping me @hectorerb in the IRC chatroom if you get stuck.

## Contribute

Want to file a bug, contribute some code, or improve documentation? Excellent! Read up on our
guidelines for [contributing](./CONTRIBUTING.md) and then check out one of our issues in the [Issues Dashboard](https://github.com/flyve-mdm/flyve-mdm-inventory/issues).

## Copying

* **Name**: [Flyve MDM](https://flyve-mdm.com/) is a registered trademark of [Teclib'](http://www.teclib-edition.com/en/).
* **Code**: you can redistribute it and/or modify
    it under the terms of the GNU General Public License ([GPLv3](https://www.gnu.org/licenses/gpl-3.0.en.html)).
* **Documentation**: released under Attribution 4.0 International ([CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)).
