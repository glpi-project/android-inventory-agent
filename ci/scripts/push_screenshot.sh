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

# Push commits and tags to origin branch
sudo mv ./fastlane/metadata/android ./screenshots
sudo mv ./screenshots/screenshots.html ./screenshots/index.html

# send to gh-pages, also removes folder with old docs
yarn gh-pages --dist ./screenshots/ --dest ./screenshots/ -m "ci(screenshot): update screenshot"

# Update headers for correct display on project site

# checkout uncommited changes
git checkout -- app/src/main/assets/setup.properties

# fetch
git fetch origin gh-pages

# move to branch
git checkout gh-pages

# clean workspace
sudo git clean -fdx

# add header
ruby ./ci/add_header_screenshot.rb

# add
git add ./screenshots

# commit headers change
git commit -m "ci(screenshots): add headers"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH