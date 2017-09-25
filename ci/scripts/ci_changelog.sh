#!/usr/bin/env bash

# git add Manifest
git add app/src/main/AndroidManifest.xml

# git commit
git commit -m "ci(manifest): update code and version manifest"

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# clean unstage file on gh-pages to remove all others files gets on checkout
sudo git clean -fdx

# remove local CHANGELOG.md on gh-pages
if [[ -e CHANGELOG.md ]]; then
    sudo rm CHANGELOG.md
fi

# get changelog from branch
git checkout $CIRCLE_BRANCH CHANGELOG.md

# Create header content to CHANGELOG.md
echo "---" > header.md
echo "layout: modal" >> header.md
echo "title: changelog" >> header.md
echo "---" >> header.md

# Duplicate CHANGELOG.md
cp CHANGELOG.md CHANGELOG_COPY.md
# Add header to CHANGELOG.md
(cat header.md ; cat CHANGELOG_COPY.md) > CHANGELOG.md
# Remove CHANGELOG_COPY.md
rm CHANGELOG_COPY.md
rm header.md

# if has change
if [[ -z $(git status -s) ]]; then
    echo "with out modifications"
else
    # create a commit
    git commit -m "build(changelog): send changelog.md to gh-page"

    # push to branch
    git push origin gh-pages
fi
    # got back to original branch
    git checkout $CIRCLE_BRANCH