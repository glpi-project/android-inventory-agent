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

# Add header to all files on the folder reports/javadoc
Dir.glob("development/coverage/**/*.html") do |search_file| # note one extra "*"
    file = File.open("#{search_file}", "r+")
    buffer = file.read
    file.rewind
    file.puts "---"
    file.puts "layout: coverage"
    file.puts "---"
    file.print buffer
    file.close

    # rename folder resources
    data = File.read("#{search_file}")
    filtered_data = data.gsub(".resources", "resources")
    File.open("#{search_file}", "w") {|file| file.puts filtered_data }

    # rename .session.html
    data = File.read("#{search_file}")
    filtered_data = data.gsub(".session", "session")
    File.open("#{search_file}", "w") {|file| file.puts filtered_data }

end

# Add header to all files on the folder androidTests
Dir.glob("development/test-reports/**/*.html") do |search_file| # note one extra "*"
    file = File.open("#{search_file}", "r+")
    buffer = file.read
    file.rewind
    file.puts "---"
    file.puts "layout: coverage"
    file.puts "---"
    file.print buffer
    file.close
end

# Add header to all files on the folder reports/javadoc
Dir.glob("development/code-documentation/**/*.html") do |search_file| # note one extra "*"
    file = File.open("#{search_file}", "r+")
    buffer = file.read
    file.rewind
    file.puts "---"
    file.puts "layout: codeDocumentation"
    file.puts "---"
    file.print buffer
    file.close
end