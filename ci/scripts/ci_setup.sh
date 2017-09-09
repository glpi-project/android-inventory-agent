#!/usr/bin/env bash

# go to folder
cd flyve

# install gems
sudo apt-get install ruby-full build-essential

# install fastlane
gem install fastlane

# install Node.js v7
curl -sL https://deb.nodesource.com/setup_7.x | sudo -E bash -
sudo sudo apt-get install -y nodejs

# install globally
sudo npm install -g conventional-github-releaser

# install node package available on package.json
npm install

# install transifex CLI
sudo apt-get -y install python-pip
pip install transifex-client
sudo echo $'[https://www.transifex.com]\nhostname = https://www.transifex.com\nusername = '"$TRANSIFEX_USER"$'\npassword = '"$TRANSIFEX_API_TOKEN"$'\ntoken = '"$TRANSIFEX_API_TOKEN"$'\n' > ~/.transifexrc

# install JQ to read JSON on bash
sudo apt-get install jq