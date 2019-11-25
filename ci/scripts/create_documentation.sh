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

GH_COMMIT_MESSAGE=$(git log --pretty=oneline -n 1 $CIRCLE_SHA1)

# validate commit message to avoid repeated builds and loops
if [[ $GH_COMMIT_MESSAGE != *"ci(release): generate CHANGELOG.md for version"* && $GH_COMMIT_MESSAGE != *"build(properties): add new properties values"* && $GH_COMMIT_MESSAGE != *"ci(release): update version on android manifest"* ]]; then

# run generate documentation script
./ci/scripts/generate_documentation.sh

# Update layouts and styles of development folder for correct display on project site

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# clean workspace
sudo git clean -fdx

# remove default stylesheet.css
sudo rm ./development/code-documentation/"$CIRCLE_BRANCH"/stylesheet.css
# sudo rm ./development/coverage/resources/report.css
# sudo rm ./development/test-reports/css/base-style.css
# sudo rm ./development/test-reports/css/style.css

# add new css
cp ./css/codeDocumentation.css ./development/code-documentation/"$CIRCLE_BRANCH"/stylesheet.css
# cp ./css/coverage.css ./development/coverage/resources/report.css
# cp ./css/testReports.css ./development/test-reports/css/style.css
# touch ./development/test-reports/css/base-style.css

# change headers
ruby ci/add_header.rb

# add and commit changes
git add . && git commit -m "docs(development): update headers and css styles"

# push to branch
git push origin gh-pages

# go back to original branch
git checkout $CIRCLE_BRANCH

fi