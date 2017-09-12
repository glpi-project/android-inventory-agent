#!/usr/bin/env bash

# Generate javadoc this folder must be on .gitignore
javadoc -d ./reports$1/javadoc -sourcepath ./app/src/main/java -subpackages .

# add reports
git add reports$1 -f

# create commit with temporary report folder
git commit -m "tmp report commit"

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# get javadoc folder
git checkout $CIRCLE_BRANCH reports$1/javadoc

# git add javadoc folder
git add reports$1/javadoc

# create commit for documentation
git commit -m "docs(javadoc): update javadoc"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH