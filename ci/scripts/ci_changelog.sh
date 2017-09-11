#!/usr/bin/env bash

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# clean unstage file on gh-pages to remove all others files gets on checkout
sudo git clean -fdx

# remove local CHANGELOG.md on gh-pages
rm CHANGELOG.md

# Create header content to work with gh-pages templates
HEADER="---"$'\r'"layout: modal"$'\r'"title: changelog"$'\r'"---"$'\r\r'

# Duplicate CHANGELOG.md
cp CHANGELOG.md CHANGELOG_COPY.md

# Add header to CHANGELOG.md
(echo $HEADER ; cat CHANGELOG_COPY.md) > CHANGELOG.md

# Remove CHANGELOG_COPY.md
rm CHANGELOG_COPY.md

# add
git add CHANGELOG.md

# create commit
git commit -m "docs(changelog): update changelog$1 with version ${GIT_TAG}"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH