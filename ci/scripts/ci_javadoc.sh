#!/usr/bin/env bash

# Generate javadoc this folder must be on .gitignore
javadoc -d ./reports$1/javadoc -sourcepath ./app/src/main/java -subpackages . -nonavbar

# delete the index.html file
sudo rm ./reports$1/javadoc/index.html

# rename the overview-summary.html file toindex.html
mv ./reports$1/javadoc/overview-summary.html ./reports$1/javadoc/index.html

# add header
ruby ci/scripts/add_javadoc_header.rb

# remove default stylesheet.css
sudo rm ./reports$1/javadoc/stylesheet.css

# get stylesheet.css template
wget -qO- https://gist.githubusercontent.com/flyve-mdm-bot/78014d4ffe3d5d70585a7b538f7eb84c/raw/fd12955bc582d968472a6d7f8b78ca5b8d4b8a23/stylesheet.css -O ./reports$1/javadoc/stylesheet.css

# add reports
git add reports$1 -f

# create commit with temporary report folder
git commit -m "tmp report commit"

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# delete old javadoc folder
sudo rm -R reports$1/javadoc

# get fresh javadoc folder
git checkout $CIRCLE_BRANCH reports$1/javadoc

# git add javadoc folder
git add reports$1/javadoc

# create commit for documentation
git commit -m "docs(javadoc): update javadoc"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCHgit checkout $CIRCLE_BRANCH