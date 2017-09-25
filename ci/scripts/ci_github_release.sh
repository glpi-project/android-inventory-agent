#!/usr/bin/env bash

# push tag to github
# conventional-github-releaser -p angular -t $GH_TOKEN -r 0
GIT_TAG=$(jq -r ".version" package.json)

# Create zip example code
sudo zip -r $CIRCLE_ARTIFACTS/InventoryAgent-$GIT_TAG.zip app/build/outputs/apk/app-release.apk

# Update release name
github-release release \
--user $CIRCLE_PROJECT_USERNAME \
--repo $CIRCLE_PROJECT_REPONAME \
--tag ${GIT_TAG} \
--name "Inventory Agent v${GIT_TAG}" \
--description "$(git log -1 --pretty=%B)"

# Upload example code release
github-release upload \
--user $CIRCLE_PROJECT_USERNAME \
--repo $CIRCLE_PROJECT_REPONAME \
--tag ${GIT_TAG} \
--name "Inventory Agent zip" \
--file $CIRCLE_ARTIFACTS/InventoryAgent-$GIT_TAG.zip