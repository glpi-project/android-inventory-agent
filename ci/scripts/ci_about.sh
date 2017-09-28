#!/usr/bin/env bash

sudo rm app/src/main/assets/about.properties.md

GIT_TAG=$(jq -r ".version" package.json)

echo "about.version=${GIT_TAG}" >> app/src/main/assets/about.properties.md
echo "about.build=$CIRCLE_BUILD_NUM" >> app/src/main/assets/about.properties.md
echo "about.date=$(date "+%a %b %d %H:%M:%S %Y")" >> app/src/main/assets/about.properties.md
echo "about.commit=$(git rev-parse --verify --short=7 HEAD)" >> app/src/main/assets/about.properties.md