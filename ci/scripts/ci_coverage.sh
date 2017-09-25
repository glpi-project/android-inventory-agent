#!/usr/bin/env bash

# create code coverage report
./gradlew createDebugCoverageReport

# move code coverage
mv -v inventory/build/reports/coverage reports$1

#move Android test
mv -v inventory/build/reports/androidTests reports$1

# replace .resources with resource because github don't support folders with "_" or "." at the beginning
mv reports$1/debug/.resources reports$1/debug/resources

# replace .sessions
mv reports$1/debug/.sessions.html reports$1/debug/sessions.html

# add header
ruby ci/scripts/add_coverage_header.rb

# add code coverage and test result
git add reports$1 -f

# temporal commit
git commit -m "tmp reports"

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# clean
sudo git clean -fdx

# remove report folder
sudo rm -R reports$1

# get documentation folder
git checkout $CIRCLE_BRANCH reports$1

# create commit
git commit -m "docs(coverage): update code coverage and test result"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH