#!/usr/bin/env bash

# increment version code, need to be unique to send to store
gradle updateVersionCode -P vCode=$CIRCLE_BUILD_NUM

# increment version on package.json, create tag and commit with changelog
npm run release -- -m "ci(release): generate **CHANGELOG.md** for version %s"

# Get version number from package.json
export GIT_TAG=$(jq -r ".version" package.json)

# update version name generate on package json
gradle updateVersionName -P vName=$GIT_TAG