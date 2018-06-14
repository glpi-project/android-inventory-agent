#!/usr/bin/env bash

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# clean unstage file on gh-pages to remove all others files gets on checkout
sudo git clean -fdx

# remove old CHANGELOG.md on gh-pages
sudo rm _includes/CHANGELOG.md

# get changelog from branch
git checkout $CIRCLE_BRANCH CHANGELOG.md

# move changelog to _includes folder for correct display
sudo mv CHANGELOG.md _includes/CHANGELOG.md

# add changelog
git add _includes/CHANGELOG.md && git add CHANGELOG.md

# create a commit
git commit -m "ci(changelog): update ChangeLog"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH