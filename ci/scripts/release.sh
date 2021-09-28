#!/usr/bin/env bash
#
# ---------------------------------------------------------------------
# GLPI Android Inventory Agent
# Copyright (C) 2019 Teclib.
#
# https://glpi-project.org
#
# Based on Flyve MDM Inventory Agent For Android
# Copyright © 2018 Teclib. All rights reserved.
#
# ---------------------------------------------------------------------
#
#  LICENSE
#
#  This file is part of GLPI Android Inventory Agent.
#
#  GLPI Android Inventory Agent is a subproject of GLPI.
#
#  GLPI Android Inventory Agent is free software: you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 3
#  of the License, or (at your option) any later version.
#
#  GLPI Android Inventory Agent is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  ---------------------------------------------------------------------
#  @copyright Copyright © 2019 Teclib. All rights reserved.
#  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
#  @link      https://github.com/glpi-project/android-inventory-agent
#  @link      https://glpi-project.org/glpi-network/
#  ---------------------------------------------------------------------
#

# Get version number from package.json
export GIT_TAG=$(jq -r ".version" package.json)
export GITHUB_TOKEN=$GH_TOKEN

# Generate CHANGELOG.md and increment version
IS_PRERELEASE="$( cut -d '-' -f 2 <<< "$GIT_TAG" )";

# update manifest changes
git add app/src/main/AndroidManifest.xml
git commit -m "build(manifest): increase version value"

#if [[ $CIRCLE_BRANCH != "$IS_PRERELEASE" ]]; then
#
#  PREFIX_PRERELEASE="$( cut -d '.' -f 1 <<< "$IS_PRERELEASE" )";
#  yarn release --skip.bump=true -m "ci(release): generate CHANGELOG.md for version %s" --prerelease "$PREFIX_PRERELEASE"
#
#else

# create CHANGELOG and update the number on package.json
yarn release --skip.bump=true -m "ci(release): generate CHANGELOG.md for version %s"

#fi

# send changelog to gh-pages
#yarn gh-pages --dist ./ --src CHANGELOG.md --dest ./_includes/ --add -m "docs(changelog): update changelog with version ${GIT_TAG}"

# remove from stash
git checkout app/src/main/assets/about.properties

# remove others files
git checkout . -f

# Push commits and tags to origin branch
git push --follow-tags origin $CIRCLE_BRANCH

# Create release with conventional-github-releaser
yarn conventional-github-releaser -p angular -t $GH_TOKEN 2> /dev/null || true

# get apk path
# export FILE="./app/build/outputs/apk/release/appCertified.apk"

#if [[ $CIRCLE_BRANCH != "$IS_PRERELEASE" ]]; then

    # Upload release
#     yarn github-release upload \
#     --user "${CIRCLE_PROJECT_USERNAME}" \
#     --repo "${CIRCLE_PROJECT_REPONAME}" \
#     --tag "${GIT_TAG}" \
#     --name "InventoryAgent-${GIT_TAG}.apk"

# else

    # Upload pre-release
#     yarn github-release upload \
#     --user "${CIRCLE_PROJECT_USERNAME}" \
#     --repo "${CIRCLE_PROJECT_REPONAME}" \
#     --tag "${GIT_TAG}" \
#     --name "InventoryAgent-${GIT_TAG}.apk" \
#     --pre-release

# fi

# Update develop branch
git add .
git stash
git fetch origin develop
git checkout develop
git merge $CIRCLE_BRANCH
git push origin develop --force