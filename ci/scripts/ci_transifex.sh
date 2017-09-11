#!/usr/bin/env bash

# install transifex CLI
sudo apt-get -y install python-pip
sudo pip install transifex-client
sudo echo $'[https://www.transifex.com]\nhostname = https://www.transifex.com\nusername = '"$TRANSIFEX_USER"$'\npassword = '"$TRANSIFEX_API_TOKEN"$'\ntoken = '"$TRANSIFEX_API_TOKEN"$'\n' > ~/.transifexrc

# get transifex status
tx status

# push local files to transifex
tx push -s -t

# pull all the new language with 80% complete
tx pull -a

# add all changes
git add .

# commit this changes
git commit -m "ci(transifex): update locales files"

git push origin $CIRCLE_BRANCH