#!/usr/bin/env bash

# accept all Android Licenses
yes | sdkmanager --licenses || true

# create enviroment vars to work with fastlane telegram
echo TELEGRAM_WEBHOOKS=$TELEGRAM_WEBHOOKS > .env
echo GIT_REPO=$CIRCLE_REPOSITORY_URL >> .env
echo GIT_BRANCH=$CIRCLE_BRANCH >> .env

# decrypt deploy on google play file
openssl aes-256-cbc -d -out ci/gplay.json -in ci/gplay.json.enc -k $ENCRYPTED_KEY

# install gems
sudo apt-get install ruby-full build-essential

# install fastlane
sudo gem install fastlane --no-rdoc --no-ri

# install Node.js v7
curl -sL https://deb.nodesource.com/setup_7.x | sudo -E bash -
sudo sudo apt-get install -y nodejs

# install globally
sudo npm install -g conventional-github-releaser

# Install node-github-release to create and edit releases on Github
sudo npm install -g node-github-release

# install node package available on package.json
sudo npm install

# config git
git config --global user.email $GITHUB_EMAIL
git config --global user.name "Teclib' bot"
git remote remove origin
git remote add origin https://$GITHUB_USER:$GITHUB_TOKEN@github.com/$CIRCLE_PROJECT_USERNAME/$CIRCLE_PROJECT_REPONAME.git
