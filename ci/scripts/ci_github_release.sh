#!/usr/bin/env bash

# push tag to github
conventional-github-releaser -p angular -t $GITHUB_TOKEN -r 0 2> /dev/null || true

GIT_TAG=$(jq -r ".version" package.json)

# Update release name
github-release edit \
--user $CIRCLE_PROJECT_USERNAME \
--repo $CIRCLE_PROJECT_REPONAME \
--tag ${GIT_TAG} \
--name "Inventory Agent v${GIT_TAG}" \

# Upload example code release
github-release upload \
--user $CIRCLE_PROJECT_USERNAME \
--repo $CIRCLE_PROJECT_REPONAME \
--tag ${GIT_TAG} \
--name "InventoryAgent-${GIT_TAG}.apk" \
--file app/build/outputs/apk/app-release.apk