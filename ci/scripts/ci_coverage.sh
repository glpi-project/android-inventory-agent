#!/usr/bin/env bash

# create code coverage report
./gradlew createDebugCoverageReport

# get gh-pages branch
git fetch origin gh-pages

# move to gh-pages
git checkout gh-pages

# move code coverage
mv -v app/build/reports/coverage reports$1

#move Android test
mv -v app/build/reports/androidTests reports$1

# replace .resources with resource because github don't support folders with "_" or "." at the beginning
mv reports$1/coverage/debug/.resources reports$1/coverage/debug/resources

index=$(<reports$1/coverage/debug/index.html)
newindex="${index//.resources/resources}"
echo $newindex > reports$1/coverage/debug/index.html

# add code coverage
git add reports$1/coverage

# add Android Tests
git add reports$1/androidTests

# create commit
git commit -m "docs(coverage): update code coverage"

# push to branch
git push origin gh-pages

# got back to original branch
git checkout $CIRCLE_BRANCH