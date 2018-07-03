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

# move to screenshots, it must be on gitignore
sudo mv ./fastlane/metadata/android ./screenshots
sudo mv ./screenshots/screenshots.html ./screenshots/index.html

# send to gh-pages, also removes the old folder
yarn gh-pages --dist ./screenshots/ --dest ./screenshots/ -m "ci(screenshot): update screenshots"

# Update headers for correct display

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

# commit
git commit -m "ci(screenshot): add headers"

# push to branch
git push origin gh-pages

# go back to original branch
git checkout $CIRCLE_BRANCH