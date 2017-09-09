#!/usr/bin/env bash

# decrypt deploy on google play file
openssl aes-256-cbc -d -out ci/gplay.json -in ci/gplay.json.enc -k $ENCRYPTED_KEY

# send to google play
fastlane android $1 storepass:'$KEYSTORE' keypass:'$ALIAS'

