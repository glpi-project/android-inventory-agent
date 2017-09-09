#!/usr/bin/env bash

# install gems
sudo apt-get install ruby-full build-essential

# install fastlane
sudo gem install fastlane

# install Node.js v7
curl -sL https://deb.nodesource.com/setup_7.x | sudo -E bash -
sudo sudo apt-get install -y nodejs

# install globally
sudo npm install -g conventional-github-releaser

# install node package available on package.json
sudo npm install

# install transifex CLI
sudo apt-get -y install python-pip
sudo pip install transifex-client
sudo echo $'[https://www.transifex.com]\nhostname = https://www.transifex.com\nusername = '"$TRANSIFEX_USER"$'\npassword = '"$TRANSIFEX_API_TOKEN"$'\ntoken = '"$TRANSIFEX_API_TOKEN"$'\n' > ~/.transifexrc

# config git
git config --global user.email $GH_EMAIL
git config --global user.name "Flyve MDM"
#git remote remove origin
#git remote add origin https://$GH_USER:$GH_TOKEN@github.com/$TRAVIS_REPO_SLUG.git
