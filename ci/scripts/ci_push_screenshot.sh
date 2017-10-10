#!/usr/bin/env bash

# Push commits and tags to origin branch
sudo mv ./fastlane/metadata/android ./screenshots
sudo mv ./fastlane/metadata/android/screenshots.html ./screenshots/index.html

# add
git add ./screenshots

# temporal commit
git commit -m "ci(tmp): temporal commit"

# fetch
git fetch origin gh-pages

# move to branch
git checkout gh-pages

# clean workspace
sudo git clean -fdx

# git get screenshots
git checkout $CIRCLE_BRANCH ./screenshots

# add
git add ./screenshots

# commit
git commit -m "ci(screenshot): update screenshot"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH