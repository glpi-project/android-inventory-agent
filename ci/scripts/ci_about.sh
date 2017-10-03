#!/usr/bin/env bash

GIT_TAG=$(jq -r ".version" package.json)

echo "about.version=${GIT_TAG}" > app/src/main/assets/about.properties
echo "about.build=$CIRCLE_BUILD_NUM" >> app/src/main/assets/about.properties
echo "about.date=$(date "+%a %b %d %H:%M:%S %Y")" >> app/src/main/assets/about.properties
echo "about.commit=$(git rev-parse --verify --short=7 HEAD~1)" >> app/src/main/assets/about.properties
echo "about.commitFull=$(git rev-parse --verify HEAD~1)" >> app/src/main/assets/about.properties
echo "about.github=https://github.com/flyve-mdm/flyve-mdm-android-inventory-agent" >> app/src/main/assets/about.properties