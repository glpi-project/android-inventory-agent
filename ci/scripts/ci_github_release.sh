#!/usr/bin/env bash
#
#  LICENSE
#
#  This file is part of Flyve MDM Inventory Agent for Android.
#
#  Inventory Agent for Android is a subproject of Flyve MDM. Flyve MDM is a 
#  mobile device management software.
#
#  Flyve MDM Inventory Agent for Android is free software: you can redistribute 
#  it and/or modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 3
#  of the License, or (at your option) any later version.
#
#  Flyve MDM Inventory Agent for Android is distributed in the hope that it will 
#  be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  ------------------------------------------------------------------------------
#  @author    Rafael Hernandez - <rhernandez@teclib.com>
#  @author    Naylin Medina    - <nmedina@teclib.com>
#  @copyright Copyright (c) Teclib'
#  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
#  @link      https://github.com/flyve-mdm/android-mdm-dashboard/
#  @link      http://flyve.org/android-mdm-dashboard/
#  @link      https://flyve-mdm.com/
#  ------------------------------------------------------------------------------
#

# push tag to github
yarn conventional-github-releaser -p angular -t $GITHUB_TOKEN -r 0 2> /dev/null || true

# get tag number
GIT_TAG=$(jq -r ".version" package.json)

# get apk path
FILE=$(find ./app/build/outputs/apk/release -name '*.apk')

# Update release name
yarn github-release edit \
--user $CIRCLE_PROJECT_USERNAME \
--repo $CIRCLE_PROJECT_REPONAME \
--tag ${GIT_TAG} \
--name "Inventory Agent v${GIT_TAG}" \

# Upload example code release
yarn github-release upload \
--user $CIRCLE_PROJECT_USERNAME \
--repo $CIRCLE_PROJECT_REPONAME \
--tag ${GIT_TAG} \
--name "InventoryAgent-${GIT_TAG}.apk" \
--file ${FILE}