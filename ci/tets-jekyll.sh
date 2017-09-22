# gem install jekyll bundler
# bundle exec jekyll build
# rm -rf _site/reports
# ./node_modules/.bin/htmlhint _site


#!/usr/bin/env bash
set -e # halt script on error

bundle exec jekyll build
bundle exec htmlproofer ./_site