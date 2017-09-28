#!/usr/bin/env bash

if [[ -e app/src/main/assets/about.properties ]]; then
    sudo rm app/src/main/assets/about.properties
fi

GIT_TAG=$(jq -r ".version" package.json)

echo "about.version=${GIT_TAG}" >> app/src/main/assets/about.properties
echo "about.build=$CIRCLE_BUILD_NUM" >> app/src/main/assets/about.properties
echo "about.date=$(date "+%a %b %d %H:%M:%S %Y")" >> app/src/main/assets/about.properties
echo "about.commit=$(git rev-parse --verify --short=7 HEAD)" >> app/src/main/assets/about.properties
echo "about.commitFull=$(git rev-parse --verify HEAD)" >> app/src/main/assets/about.properties