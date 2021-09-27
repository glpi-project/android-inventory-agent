#!/usr/bin/env bash
#
# ---------------------------------------------------------------------
# GLPI Android Inventory Agent
# Copyright (C) 2019 Teclib.
#
# https://glpi-project.org
#
# Based on Flyve MDM Inventory Agent For Android
# Copyright © 2018 Teclib. All rights reserved.
#
# ---------------------------------------------------------------------
#
#  LICENSE
#
#  This file is part of GLPI Android Inventory Agent.
#
#  GLPI Android Inventory Agent is a subproject of GLPI.
#
#  GLPI Android Inventory Agent is free software: you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 3
#  of the License, or (at your option) any later version.
#
#  GLPI Android Inventory Agent is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  ---------------------------------------------------------------------
#  @copyright Copyright © 2019 Teclib. All rights reserved.
#  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
#  @link      https://github.com/glpi-project/android-inventory-agent
#  @link      https://glpi-project.org/glpi-network/
#  ---------------------------------------------------------------------
#

# # create code coverage report
# ./gradlew createDebugCoverageReport
#
# # move code coverage
# mv -v app/build/reports/coverage development
#
# # move Android test
# mv -v app/build/reports/androidTests development
#
# # rename folders to match respective section on project site
# mv development/debug development/coverage
# mv development/androidTests/connected development/test-reports
#
# # replace .resources with resource because github doesn't support folders with "_" or "." at the beginning
# mv development/coverage/.resources development/coverage/resources
#
# # find and replace links to the old name of file
# grep -rl .resources development/coverage/ | xargs sed -i 's|.resources|resources|g'
#
# # replace .sessions
# mv development/coverage/.sessions.html development/coverage/sessions.html
#
# # find and replace links to the old name of file
# grep -rl .sessions.html development/coverage/ | xargs sed -i 's|.sessions.html|sessions.html|g'

DOC_PATH="development/code-documentation/$CIRCLE_BRANCH"

# install jre 8 for use by javadoc
sudo apt-get -y install openjdk-8-jre

# force javadoc to use jre 8
sudo update-alternatives --config javadoc
sudo alternatives --set javadoc /usr/lib/jvm/java-8-openjdk-amd64/bin/javadoc

# Generate javadoc this folder must be on .gitignore
javadoc -d $DOC_PATH -sourcepath ./app/src/main/java -subpackages org

# delete the index.html file
sudo rm $DOC_PATH/index.html

# rename the overview-summary.html file to index.html
mv $DOC_PATH/overview-summary.html $DOC_PATH/index.html

# find and replace links to the old name of file
grep -rl overview-summary.html $DOC_PATH | xargs sed -i 's|overview-summary.html|index.html|g'

# send development folder to project site with the documentation updated, also removes the folder with old docs
yarn gh-pages --dist $DOC_PATH --dest $DOC_PATH -m "docs(development): update code documentation"