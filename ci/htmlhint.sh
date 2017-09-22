gem install jekyll bundler
bundle exec jekyll build
rm -rf _site/reports
./node_modules/.bin/htmlhint _site