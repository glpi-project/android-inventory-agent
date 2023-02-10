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

# create environment vars to work with fastlane telegram
echo GIT_REPO=$CIRCLE_REPOSITORY_URL >> .env
echo GIT_BRANCH=$CIRCLE_BRANCH >> .env

# decrypt deploy on google play file
openssl aes-256-cbc -d -out ci/gplay.json -in ci/gplay.json.enc -k $ENCRYPTED_KEY

curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add

# Maintenance commands
sudo apt-get update

# install gems
sudo apt-get install ruby-full build-essential

sudo apt-get install ruby-full
# update Rubygems
sudo gem update --system --no-document

# install rake
sudo gem install rake

# install fastlane
sudo gem install fastlane --no-document

# update bundler
sudo gem install bundler --no-document

# update Gemfile.lock
sudo bundler update --bundler

# install node package available on package.json
yarn install

# config git
git config --global user.email $GH_EMAIL
git config --global user.name "Teclib"

git remote remove origin
git remote add origin https://$GH_USER:$GH_TOKEN@github.com/$CIRCLE_PROJECT_USERNAME/$CIRCLE_PROJECT_REPONAME.git

# Get version number from package.json
export GIT_TAG=$(jq -r ".version" package.json)

# remove python2 python3
sudo apt -y purge python2.7
sudo apt-get -y install python3-pip

# install transifex CLI
sudo pip3 install --upgrade requests urllib3 botocore awscli awsebcli
sudo pip3 install transifex-client
sudo echo $'[https://www.transifex.com]\nhostname = https://www.transifex.com\nusername = '"$TRANSIFEX_USER"$'\npassword = '"$TRANSIFEX_TOKEN"$'\ntoken = '"$TRANSIFEX_TOKEN"$'\n' > ~/.transifexrc
