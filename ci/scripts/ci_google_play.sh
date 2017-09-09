#!/usr/bin/env bash

# send to google play
fastlane android $1 storepass:'$KEYSTORE' keypass:'$ALIAS'