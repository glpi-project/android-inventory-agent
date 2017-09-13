#!/usr/bin/env bash

# increment version code, need to be unique to send to store
./gradlew updateVersionCode -P vCode=$CIRCLE_BUILD_NUM

# increment version on package.json, create tag and commit with changelog
npm run release -- -m "ci(release): generate **CHANGELOG.md** for version %s"

# Get version number from package.json
export GIT_TAG=$(jq -r ".version" package.json)

# update version name generate on package json
./gradlew updateVersionName -P vName=$GIT_TAG

# git add app/src/main/AndroidManifest.xml
#
# git commit -m "ci(release): update version ($GIT_TAG) and code number ($CIRCLE_BUILD_NUM)"
#
# git push origin $CIRCLE_BRANCH