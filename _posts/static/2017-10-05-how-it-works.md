---
layout: post
permalink: howtos/how-it-works
howtos: true
published: true
title: How it works
description: A brief introduction
category: user
---

## Scan

The Inventory Agent will scan your device and gather all the hardware and software data, such as memory, software, controllers, battery, etc., and send it to your GLPI server adress.

<br />

<div>
<img src="{{ 'images/posts/inventory-main.png' | absolute_url }}" alt="Inventory Agent Main" height="500">

<img src="{{ 'images/posts/my-device-inventory.png' | absolute_url }}" alt="Show my inventory" height="500">

</div>

## Server address

Before running your inventory, you must define your server address and credentials, so the inventory can be sent to your instance.

The inventory can be send to:

- Fusion Inventory for GLPI 2.3.x and higher
- OCSInventory NG (ocsng) 1.3.x and 2.x
- Mandriva Pulse2

It requires Android 1.6 or higher.

<br />

<div>
<img src="{{ 'images/posts/show-server.png' | absolute_url }}" alt="Inventory Agent Main" height="500">

<img src="{{ 'images/posts/select-server.png' | absolute_url }}" alt="Show my inventory" height="500">

</div>

## Manage your inventory

Before running your inventory, you must define your server address and credentials, so the inventory can be sent to your instance.

You have several options to manage the inventory, some of the most important:

- Show my inventory which allows you to see a display of your inventory.
- Schedule the inventory to automatize the creation of the inventory.
- Disable/Enable the inventory.