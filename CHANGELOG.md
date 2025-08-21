# Changelog

All notable changes to this project will be documented in this file.

## [UNRELEASED]

### 💼 Other

- *(Build)* Move to SDK 35

### 💼 Other

- *(Build)* Fix trigger for CI

## [1.6.1] - 2024-12-20

### 🐛 Bug Fixes

- *(EMM)* Fix missing managed configurations used as NULL
- *(Core)* Exclude sensitive strings
- *(UI)* Adjust color scheme in user settings for better visibility and consistency.


### 💼 Other

- *(Dependencies)* Bump Android Inventory Library to 1.6.1
- *(Build)* Use main branch
- *(Build)* Clean unused step
- *(Build)* Clean composer step
- *(Build)* Use main branch for deploy alpha

## [1.6.0] - 2024-10-29

### 💼 Other

- *(Build)* Bump github action
- *(deps-dev)* Bump gh-pages from 2.2.0 to 5.0.0
- *(Build)* Bump dependencies
- *(Build)* Remove dependencies
- *(Build)* Move EndBug/add-and-commit to v9.1.1
- *(Build)* Move EndBug/add-and-commit to v9.0.0
- *(Build)* Do not add file that no longer exists
- *(Build)* Add missing package.json
- *(Build)* Revert change
- *(Build)* Move to SDK 34
- *(core)* Fix android 14 compatibility

### ⚙️ Miscellaneous Tasks

- *(release)* Update version on android manifest
- *(release)* Release new version 1.4.0

### 💼 Other

- *(Core)* Handle EMM managed configurations

## [1.5.0] - 2023-12-18

### ⚙️ Miscellaneous Tasks

- *(release)* Update version on android manifest
- *(release)* Release new version 1.4.0

## [1.4.0] - 2023-11-16

### 🚀 Features

- *(Inventory)* Added option to override serial number

### 💼 Other

- *(deps)* Bump minimatch, decode-uri-component, semver-regex

### ⚙️ Miscellaneous Tasks

- *(release)* Release new version 1.3.0
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest

## [1.3.0] - 2023-09-04

### 🚀 Features

- *(build)* Migrate develop PullRequest CircleCI to GithubAction
- *(build)* Migrate develop Push CircleCI to GithubAction
- *(core)* Bump SdkVersion to 33
- *(build)* Migrate develop Push CircleCI to GithubAction (V2)
- *(test)* Move to OS version 31
- *(build)* Release workflow with GithubAction
- *(doc)* Update README

### 🐛 Bug Fixes

- *(IntentService)* Use FLAG_MUTABLE
- *(build)* Downgrade mockito
- *(build)* Set changesNotSentForReview parameter
- *(build)* Remove old CI workflow
- *(build)* Remove useless gradle process
- *(release)* Fix GithubAction workflow
- *(release)* Fix GithubAction workflow
- *(core)* Remove useless permission
- *(core)* Remove useless permission
- *(core)* Remove useless permission
- *(build)* Capability to retrieve VersionCode
- *(build)* Fix push to google play
- *(build)* Fix push to google play
- *(permissions)* Remove useless permissions
- *(build)* Fix changesNotSentForReview

### ⚙️ Miscellaneous Tasks

- *(release)* Update version on android manifest
- *(release)* Release new version 1.3.0

## [1.2.0] - 2022-04-21

### 🚀 Features

- *(core)* Native inventory compatibility
- *(core)* Bump Compil / Target SdkVersion to 30
- *(core)* Use innentory library as external lib instead of dependencies
- *(readme)* Update readme

### 🐛 Bug Fixes

- *(release)* Fix changelog
- *(build)* Fix version value
- *(share)* Fix file path
- *(share)* Share only XML file

### 💼 Other

- *(manifest)* Increase version value
- *(deps)* Bump minimist from 0.2.1 to 1.2.6
- *(deps)* Bump trim-off-newlines from 1.0.2 to 1.0.3
- *(deps)* Bump async from 2.6.3 to 2.6.4
- *(core)* Bump version
- *(core)* Bump version from package

### ⚙️ Miscellaneous Tasks

- *(release)* Update version on android manifest
- *(release)* Generate CHANGELOG.md for version 1.2.0

## [1.1.0] - 2021-09-30

### 🚀 Features

- *(about)* Add GitHub infos
- *(share)* Inform user of the risks of sharing
- *(ui)* Allow disable notification from itself
- *(core)* Manage on startup option from qrcode or deeplink
- *(dependencies)* Upgrade yarn packages

### 🐛 Bug Fixes

- *(changelog)* Clean changelog
- *(ui)* Manage backpressed on fragment
- *(ui)* Better managment for back pressed
- *(core)* Fix start app on device boot completed
- *(build)* Install latest version for python
- *(build)* Replace useless JAVA option
- *(build)* Bump version for urllib3
- *(build)* Upgrade request first
- *(doc)* Remove useless arg from JDK 11
- *(doc)* Fix javadoc args
- *(core)* Security fix, upgrade package minimist and dot-prop
- *(doc)* Fix javadoc args
- *(doc)* Use jre 8 instead of 11
- *(doc)* Use jre 8 instead of 11
- *(inventory)* Manage cyrillic content
- *(build)* Javadoc fix
- *(doc)* Fix generated doc
- *(build)* Update somes packages
- *(build)* Install rake gem
- *(build)* MinSDK to 19 for UIAutomator test
- *(build)* Fix javadoc generation
- *(build)* Disable create_doc from circleci
- *(fastlane)* Set changes_not_sent_for_review arg
- *(fastlane)* Set changes_not_sent_for_review arg
- *(boot)* Fix boot for API >= 26 (as forground service)
- *(core)* Fix default value
- *(boot)* Fix boot for API < 26
- *(readme)* Readme review for professional support
- *(readme)* Fix GLPI Agent version

### 💼 Other

- *(deps)* Bump lodash from 4.17.15 to 4.17.19
- *(deps-dev)* Bump standard-version from 7.0.1 to 8.0.1
- *(gradle)* Update gradle to 4.0.2
- *(deps)* Bump ini from 1.3.5 to 1.3.7
- *(deps)* Bump path-parse from 1.0.6 to 1.0.7
- *(deps)* Bump addressable from 2.7.0 to 2.8.0
- *(deps)* Bump hosted-git-info from 2.8.5 to 2.8.9
- *(deps)* Bump lodash from 4.17.19 to 4.17.21
- *(deps)* Bump handlebars from 4.5.3 to 4.7.7
- *(deps)* Bump y18n from 4.0.0 to 4.0.1
- *(help)* Change GLPI Network URL
- *(readme)* Update content
- *(core)* Bump version
- *(deps)* Bump ansi-regex from 5.0.0 to 5.0.1
- *(release)* Disable github prerelease
- *(yarn)* Upgrade packages
- *(manifest)* Increase version value

### 🚜 Refactor

- *(core)* Refactor notification

### ⚙️ Miscellaneous Tasks

- *(release)* Update version on android manifest
- *(release)* Generate CHANGELOG.md for version 1.1.0

## [1.0.0] - 2020-01-07

### 🚀 Features

- Add the inventory engine as dependency (#3)
- *(assets)* Add graphic resources and translations  (#16)
- *(acra)* Add acra library
- *(acra)* Configuring acra
- *(acra)* Set tracepot like server and add aditional setup
- *(acra)* Remove acra resources
- *(bugsnag)* Implement basic bugsnag feature
- *(bugsnag)* Add custom endpoint
- *(about)* Add about screen
- *(about)* Add toolbar
- *(about)* Add html and properties information
- *(privacy)* Add privacy functionality
- *(about)* Add preference about item
- *(inventory)* Send inventory on thread
- *(inventory)* Add progress dialog running inventory
- *(show)* Add inventory activity and adapter
- *(notification)* Send message to notification bar
- *(easteregg)* Add easter egg crash report
- *(share)* Add share button
- *(library)* Update inventory library
- *(inventory)* Add description information on version client
- *(privacy)* Add privacy on anonymous data
- *(design)* Add main activity
- *(design)* Add drawer menu adapter
- *(design)* Update main activity layout
- *(design)* Add icons resources
- *(design)* Add list item drawer layout
- *(design)* Launch main activity
- *(design)* Add drawer menu titles
- *(design)* Add text styles
- *(design)* Update design with drawer and toolbar
- *(design)* Add default item layout for home list
- *(design)* Add check type item layout for home list
- *(design)* Add header type item layout for home list
- *(design)* Add send anonymous data method
- *(design)* Create home schema for list
- *(design)* Create home adapter
- *(design)* Add list on layout
- *(design)* Add inventory options
- *(design)* Add global parameters layout
- *(design)* Add global parameters preference
- *(design)* Add inventory parameters layout
- *(design)* Add inventory parameters preference
- *(design)* Add service and options
- *(design)* Request permission to the user
- *(design)* Implement about like fragment
- *(help)* Add help implementation
- *(menu)* Update inventory icon
- *(cache)* Add long and boolean cache method
- *(inventory)* Add count down on the service
- *(inventory)* Implement count down
- *(countdown)* Check the auto inventory to show the countdown
- *(permission)* Add request permission screen
- *(language)* Add catalan language
- *(inventory)* Add tablayout
- *(inventory)* Add viewpager and show info
- *(inventory)* Remove header in specific tab
- *(inventory)* Remove tab to empty information
- *(icon)* Add adaptive icon to android oreo
- *(inventory)* Changed style tab information
- *(inventory)* Add line separate and background color list
- *(tag)* Send tag to inventory
- *(servers)* Add button and list server activity
- *(servers)* Added list view servers
- *(servers)* New dialog to list servers
- *(servers)* Limit size name list server in dialog
- *(servers)* Add tag to time alarm and service
- *(servers)* Validate empty server and show message
- *(servers)* Add button close dialog
- *(inventory)* Send inventory to all servers
- *(language)* Add catalan language
- *(inventory)* Add tablayout
- *(inventory)* Add viewpager and show info
- *(inventory)* Remove header in specific tab
- *(inventory)* Remove tab to empty information
- *(icon)* Add adaptive icon to android oreo
- *(inventory)* Changed style tab information
- *(inventory)* Add line separate and background color list
- *(tag)* Send tag to inventory
- *(servers)* Add button and list server activity
- *(servers)* Added list view servers
- *(servers)* New dialog to list servers
- *(servers)* Limit size name list server in dialog
- *(servers)* Add tag to time alarm and service
- *(servers)* Validate empty server and show message
- *(servers)* Add button close dialog
- *(inventory)* Send inventory to all servers
- *(categories)* Add views and button
- *(categories)* MVC to categories
- *(categories)* Show specific categories in resport
- *(categories)* Validate format string to categories
- *(text)* Add property to enable select text
- *(transifex)* Add values show inventory in string resource
- *(transifex)* Validate input in header to the screen inventory
- *(transifex)* Get language device
- Improve memory to get information inventory
- *(core)* Update circleci image
- *(inventory)* Bump inventory lib 0.12.0 to 1.3.6
- *(core)* Update branding and package name
- *(core)* Update license
- *(core)* Update README
- *(readme)* Change logo
- *(inventory)* Bump android inventory library from 1.3.6 to 1.4.0
- *(core)* Add firebase crash report
- *(core)* Add QR code scanner to add servers informations
- *(core)* Add DeepLink to add servers informations
- *(core)* Rename app
- *(build)* Bump fastlane v2.137.0 to v2.138.0
- *(readme)* Add badges
- *(readme)* Add screenshots
- *(core)* Manage schedule inventory with deeplink and qrcode

### 🐛 Bug Fixes

- *(connection)* Allow work on main thread
- *(data)* Fix anonymous data summary text
- *(inventory)* Validate http response
- *(about)* Close link tag on last commit link
- *(inventory)* Validate headers response
- *(toolbar)* Fix toolbar nullpointerexception
- *(anonymous)* Add application json on request property
- *(package)* Update conventional-github-releaser to version 2.0.0
- Remove modules file created by IntelliJ IDEA
- *(alarm)* Set repeting with interval day
- *(alarm)* Check if execute the inventory
- *(inventory)* Add log messages
- *(alarm)* Update alarm repeating time
- *(inventory)* Catch empty url exception
- *(package)* Update conventional-github-releaser to version 3.0.0
- *(build)* Implement inventory library 0.8.6
- *(share)* Implement the inventory library share method
- *(ui)* Add delay on request permission popup
- *(main)* Remove request permission
- *(permission)* Move next screen from main to permission
- *(splash)* Add postdelayed direct on splash activity
- *(icon)* Shown icon app with API below 26
- *(progress)* Change behavior progress bar
- *(connection)* Validated https connection
- *(connection)* Return method when the validation is true
- *(connection)* Add default value to URL
- *(schedule)* Change type schedule to send inventory
- *(schedule)* Validated if change the schedule
- *(schedule)* Automatic inventory by default disable
- *(apk)* Changed version code in manifest
- *(service)* Validated version Android to call service
- *(service)* Added permission foreground service
- *(service)* Improved xml to send in test
- *(service)* Add parameter to inventory task
- *(service)* Add parameter to inventory task in dialog list
- *(service)* Add parameter to inventory task in time alarm
- *(service)* Add parameter to inventory task in test
- *(icon)* Shown icon app with API below 26
- *(progress)* Change behavior progress bar
- *(connection)* Validated https connection
- *(connection)* Return method when the validation is true
- *(connection)* Add default value to URL
- *(schedule)* Change type schedule to send inventory
- *(schedule)* Validated if change the schedule
- *(schedule)* Automatic inventory by default disable
- *(apk)* Changed version code in manifest
- *(service)* Validated version Android to call service
- *(service)* Added permission foreground service
- *(service)* Improved xml to send in test
- *(service)* Add parameter to inventory task
- *(service)* Add parameter to inventory task in dialog list
- *(service)* Add parameter to inventory task in time alarm
- *(service)* Add parameter to inventory task in test
- *(icon)* Change icon jpg by background color
- *(memory)* Add large heap in manifest
- *(permission)* Add missing permission
- *(security)* Update deps (yarn and gem)
- *(gradle)* Fix gradle repositories
- *(ci)* Fix javadoc generation
- *(build)* Bump mockito-android version to 2.21.0
- *(security)* Update deps (yarn and gem)
- *(security)* Update yarn package
- *(package)* Update mem to version 6.0.0
- *(core)* Fix compatibility intent with android 28
- *(build)* Update ci script
- *(build)* Fastlane step
- *(build)* Fix fastlane
- *(build)* Update fastlane to 2.137.0
- *(build)* Build gem without document
- *(fastlane)* Fix env variables
- *(fastlane)* Fix env variables and path
- *(fastlane)* Update keystore
- *(fastlane)* Env var
- *(build)* Gem update without document
- *(build)* Fix fastlane workflow
- *(fastlane)* Do not run gradle test, it's already done before
- *(fastlane)* Do not run gradle assemble
- *(build)* Bump version code
- *(about)* Retrieve informations from properties
- *(build)* Run create_about_data
- *(build)* Fix patch for create_about_data
- *(build)* Fix path for create_about_data with gradle
- *(build)* Fix version code incremental
- *(build)* Update about.properties file
- *(inventory)* Fix http user-agent informations
- *(design)* Change UI/UX
- *(build)* Add draft status
- *(log)* Improve log
- *(build)* Reload gemfile.lock
- *(readme)* Update informations
- *(build)* Prevent error on build
- *(build)* Do not run firebase test labs for teclib-bot
- *(readme)* Update informations
- *(UI)* Fix accessibility
- *(core)* Add support for HTTP
- *(ui)* Update logo
- *(ui)* Fix crash on orientation change
- *(ui)* Change icon / banner
- *(help)* Prevent error from android lollipop and WebView
- *(readme)* Fix greenkeeper badge
- *(inventory)* Set content type and charset correctly
- *(share)* Prevent error (transactiontoolarge) on share inventory
- *(build)* Do not add apk with release
- *(build)* Fix release

### 💼 Other

- Replaced following android guidelines
- Fix serial unknown when sdk > 9
- Use git hash + branch to suffix apk final name
- *(api)* Update minsdk and targetsdk
- *(version)* Update version
- *(gradle)* Update build tools version
- *(gradle)* Remove unused compile
- *(gradle)* Change build tools version
- *(gradle)* Update inventory library 0.3.0
- *(gradle)* Add gradle properties file
- *(gradle)* Update version factor
- *(settings)* Add YAML file for bot settings
- *(github)* Add app to auto invite contributors
- *(config)* Add correct usage to add team name
- *(library)* Update library
- *(gradle)* Add multidex dependecie
- *(gradle)* Remove external dependecies
- *(npm)* Fix repository slug
- *(transifex)* Fix project slug
- Fix repository slug
- *(npm)* Update standard-version
- *(eclipse)* Add buildship and gradle
- *(eclipse)* Update buildship
- *(npm)* Version ready for first release
- *(npm)* Update homepage
- *(npm)* Update record lock file
- *(gradle)* Update dependencies
- *(manifest)* Add permission request
- *(library)* Update inventory library
- *(gradle)* Add logger
- *(gradle)* Add logger
- *(manifest)* Add preference activities
- *(gradle)* Update inventory library 0.7.0
- *(gradle)* Upgrade all libraries required
- *(package)* Add library for release
- *(gradle)* Update inventory library 0.8.5
- *(gradle)* Add constraint support
- *(gradle)* Update versioncode and name method
- *(manifest)* Update version code
- *(manifest)* Increase version value
- *(gradle)* Add testOptions
- *(gradle)* Fix mockito implementation
- *(ruby)* Fix security vulnerabilities
- *(ruby)* Fix security vulnerabilities
- *(ruby)* Bundle update fastlane
- *(library)* Upgrade inventory library
- *(gradle)* Add testOptions
- *(gradle)* Fix mockito implementation
- *(ruby)* Fix security vulnerabilities
- *(ruby)* Fix security vulnerabilities
- *(ruby)* Bundle update fastlane
- *(library)* Upgrade inventory library
- *(package)* Changed version in package json
- *(manifest)* Increment the version code to 39164
- *(manifest)* Increase version value
- *(release)* Integration release rc.2
- *(release)* Integration release rc.2
- *(github)* Fix invite contributors
- *(dependencies)* Add maintenance commands
- *(gradle)* Bump version to 3.5.1
- *(circleci)* Disable firebase test lab
- *(bundle)* Update gem bundle 1.17.3 to 2.0.2
- *(core)* Bump target api level to 28
- *(core)* Bump target sdk version to 28
- *(inventory)* Update inventory library 1.4.2 to 1.4.3
- *(inventory)* Update inventory library 1.4.3 to 1.4.4
- *(docs)* Update README
- *(docs)* Update README
- *(manifest)* Increase version value

### 🚜 Refactor

- Rebranding (#7)
- *(package)* Change name on description
- *(log)* Add log wrapper class
- *(accueil)* Refactor code
- *(log)* Replace local log with FlyveLog
- *(agent)* Refactor agent code
- *(autoinventory)* Refactor auto inventory code
- *(license)* Add header license to all files
- *(todo)* Remove unused TODO comments
- *(*)* Replace local logging with FlyveLog wrapper
- *(*)* Remove unused code and files
- *(manifest)* Remove duplicated services
- *(service)* Improve inventory service
- *(service)* Restart settings with changes
- *(license)* Change link information
- *(inventory)* Refactor send inventory to work with test
- *(inventory)* Update flyve mdm inventory package
- *(url)* Update default value url to fusion inventory
- *(about)* Add space and replace some words on text
- *(locale)* Integrate all the locale string
- *(color)* Add custom colors
- *(locale)* Add resources privacy text
- *(locale)* Add resource to about title
- *(about)* Update about properties with default values
- *(app)* Change name app
- *(about)* Add url github repository on properties
- *(locale)* Unify the language in one file
- *(crashreport)* Update crash report endpoint
- *(inventory)* Update the anonymous information endpoint
- *(locale)* Improve labels descriptions
- *(error)* Improve error message
- *(snackbar)* Replace toast message with snackbar
- *(menu)* Remove right toolbar menu
- *(error)* Show proper error message with library
- *(data)* Improved anonymous data sent
- *(inventory)* Update the anonymous information endpoint
- *(environment)* Add helper to manage environment information
- *(version)* Add version on setUserAgent http protocol
- *(cache)* Remove predefined url
- *(show)* Add inventory data
- *(show)* Add design
- *(inventory)* Add progress bar
- *(notification)* Add icon for android mayor lollipop version
- *(locale)* Update notification message
- *(connection)* Add strict mode policy
- *(inventory)* Update list inventory design
- *(design)* Keep the same look and feel
- *(about)* Improve look and feel
- *(accueil)* Convert in preference activity
- *(broadcast)* Check null values
- *(permission)* Request camera permission
- *(app)* Create a global app instance
- *(alarm)* Improve send inventory task with calendar
- *(chollima)* Update endpoint
- *(accueil)* Remove comments
- *(log)* Implement logger
- *(log)* Implement logger
- *(share)* Update intent title
- *(resources)* Remove iddling recycler
- *(accueil)* Remove accueil fragment
- *(manifest)* Update activity and fragment names
- *(about)* Update layout name
- *(about)* Update layout name
- *(inventory)* Add progress dialog
- *(inventory)* Implement helpers send anonymous data
- *(home)* Add header accent color
- *(about)* Remove duplicated header
- *(*)* Reorder files on packages
- *(home)* Reorder inventory show
- *(toolbar)* Change toolbar color and improve
- *(main)* Implement MVC pattern
- *(main)* Request permission with MVC
- *(report)* Implement MVC pattern
- *(splash)* Implement MVC pattern
- *(about)* Implement MVC pattern
- *(home)* Implement MVC pattern
- *(permission)* Check if permission are granted
- *(icon)* Create mipmap folders and remove olders
- *(inventory)* Implement private data with new design
- *(gitignore)* Add screenshots folder
- *(splash)* Move validation from model to helper
- *(service)* Instance handler on create for instrumented test
- *(about)* Add new var for html convert for instrumented test
- *(servers)* Change name model to schema
- *(servers)* Remove comment code
- *(service)* Instance handler on create for instrumented test
- *(about)* Add new var for html convert for instrumented test
- *(servers)* Change name model to schema
- *(servers)* Remove comment code
- *(preferences)* Save and load array
- *(code)* Comment some code
- *(ui)* Change pics and fix float input behavior

### 📚 Documentation

- *(license)* Add GPLv3
- *(header)* Add Flyve MDM link
- *(equals)* Add param and return tags
- *(createEasySSLContext)* Add return and throws tags
- *(getSSLContext)* Add return and throws tags
- *(hashCode)* Add the return of the tag
- *(HttpInventory)* Add the param of the tag
- *(onReceive)* Add the param of the tags
- *(getDeviceID)* Add the return of the tag
- *(getUrl)* Add the return of the tag
- *(getShouldAutoStart)* Add the return of the tag
- *(getCredentialsPassword)* Add the return of the tag
- *(getCredentialsLogin)* Add the return of the tag
- *(onSharedPreferenceChanged)* Add the param of the tags
- *(onCreate)* Add the summary
- *(d)* Add the param of the tag
- *(v)* Add the param of the tag
- *(i)* Add the param of the tag
- *(e)* Add the param of the tag
- *(w)* Add the param of the tag
- *(wtf)* Add the param of the tag
- *(log)* Add the param of the tags
- *(onBind)* Add param and return tags
- *(onCreate)* Add the summary
- *(onStartCommand)* Add tags
- *(onDestroy)* Add the summary
- *(onStart)* Add param and see tags
- *(onCreate)* Add the param of the tag
- *(openActivity)* Add the summary
- *(setAlarm)* Add the param of the tag
- *(cancelAlarm)* Add the param of the tag
- *(onReceive)* Add the param of the tags
- *(sendInventory)* Add param and return tags
- *(onSharedPreferenceChanged)* Add the param of the tags
- *(onPause)* Add the summary
- *(onResume)* Add the summary
- *(onCreate)* Add the summary and param tag
- *(onStart)* Fix the summary
- *(circleci)* Update indentation
- *(readme)* Improve full content
- *(fastlane)* Add README.md for fastlane
- *(ci)* Add README.md
- *(circleci)* Add README.md
- *(transifex)* Add README.md
- *(contributing)* Create the contribute guidelines
- *(contributing)* Change title
- *(contributing)* Fix links
- *(readme)* Update content
- *(.github)* Add issue template
- *(.github)* Add pull request template
- *(github)* Rename file
- *(github)* Change code block to list
- *(github)* Change content
- *(contributing)* Fix links
- *(readme)* Add Greenkeeper badge
- *(greenkeeper)* Move badge to proper place
- *(contributing)* Remove code of conduct
- *(codeofconduct)* Add code of conduct
- *(readme)* Add compatibility matrix
- *(header)* Fix repository slug
- Fix repository slug
- *(license)* Remove GPLv2
- *(readme)* Markdown linting
- *(readme)* Markdown linting
- *(readme)* Markdown linting
- *(template)* Markdown linting
- *(changelog)* No release no changelog
- *(readme)* Remove maven central badge
- *(service)* Update params with correct names
- *(readme)* Fix links
- *(agent)* Update parameters info
- *(github)* Improve pull request template
- *(contributing)* Improve content
- *(github)* Improve issue template
- *(github)* Change issue template
- *(github)* Change pull request template
- *(contributing)* Update to new format
- *(github)* Update issue templates
- *(readme)* Add professional services
- *(settings)* Add cherry pick label
- *(header)* Add license header
- *(readme)* Update information
- *(github)* Update PR template
- *(contributing)* Add how to create a fork
- *(readme)* Add new workflow
- *(headers)* Update license
- *(readme)* Move contribute block
- *(readme)* Update inventory information
- *(readme)* Add status badge
- *(readme)* Update compatibility matrix
- *(readme)* Move contribute block
- *(readme)* Update inventory information
- *(readme)* Add status badge
- *(readme)* Update compatibility matrix
- *(header)* Add email
- *(header)* Add email and copyright
- *(readme)* Add android version compatibility
- *(separator)* Add title separator to the inventory
- *(credits)* Add credit file
- *(headers)* Remove author tag
- *(readme)* Update readme
- *(ci)* Update readme
- *(changelog)* Delete change logs of candidates

### 🎨 Styling

- *(circleci)* Update app folder name to flyve_mdm
- *(updateversion)* Remove markdown on commit text
- *(lint)* Fix markdown
- *(lint)* Fix markdown
- *(adapter)* Update header copyright
- *(core)* Update header copyright
- *(utils)* Update header copyright
- *(ui)* Update header copyright
- *(test)* Update header copyright

### 🧪 Testing

- *(*)* Add instrumented and unit test examples
- *(inventory)* Add inventory task test xml and json
- *(example)* Remove example instrumented test
- *(inventory)* Add send inventory test
- *(inventory)* Rename flyve mdm inventory package name
- *(about)* Add about test
- *(*)* Remove fragment test
- *(inventory)* Fix send inventory test with thread
- *(inventorytask)* Set the url to test
- *(inventory)* Add inventory screen shot
- *(inventory)* Preset url for instrumented test
- *(inventory)* Update inventory test
- *(idling)* Add idling resource
- *(inventory)* Refactor test
- *(inventory)* Fix inventory refactor name
- *(splash)* Remove splash test
- *(about)* Add about model test
- *(home)* Add home model test
- *(main)* Add main model test
- *(report)* Add report model test
- *(splash)* Add splash model test
- *(order)* Refactor test folder
- *(comment)* Remove unused and comment code
- *(main)* Remove main instrumented test
- *(about)* Add about model test
- *(home)* Add home model test
- *(main)* Add main model test
- *(report)* Add report model test
- *(splash)* Add splash model test
- *(order)* Refactor test folder
- *(comment)* Remove unused and comment code
- *(main)* Remove main instrumented test

### ⚙️ Miscellaneous Tasks

- *(git)* Add javadoc folder
- *(travis)* Add script to install, build and deploy on travis
- *(travis)* Google Play credentials
- *(travis)* Remove unused files
- *(travis)* Add encrypt keys
- *(fastlane)* Library to send message to telegram with fastlane
- *(fastlane)* Refactor script with beta and production deploy
- *(gradle)* Update gradle script
- *(travis)* Add precise distribution settings
- *(travis)* Add jdk 8
- *(gradle)* Upgrade gradle version
- *(gradle)* Update compile sdk version
- *(travis)* Move git checkout inside master and develop branch build
- *(travis)* Update minimum and target sdk
- *(travis)* Update android api and tools on travis script
- *(travis)* Remove jdk 8 from script
- *(deploy)* Add Google Play keys to deploy
- *(gradle)* Remove checkbuild lint options
- *(travis)* Remove jdk 8
- *(gradle)* Update min and target sdk
- *(deploy)* Add package file with project information
- *(deploy)* Enable run test on pull request
- *(test)* Run test on master, develop branch and pull request
- *(build)* Release **beta** for version 2.1.0-beta
- *(build)* Release **beta** for version 2.1.0-beta
- *(build)* Release **beta** for version 2.1.0-beta
- *(travis)* Update before script travis
- *(travis)* Update deploy script travis
- *(travis)* Update script travis
- *(travis)* Update git ignore file
- *(release)* 2.1.0
- *(build)* Release **beta** for version 2.1.0-beta
- *(release)* 2.1.1
- *(build)* Release **beta** for version 2.1.1-beta
- *(release)* 2.1.2
- *(build)* Release **beta** for version 2.1.2-beta
- *(locale)* Add transfifex push and pull
- *(build)* Change api simulator to improve performance
- *(travis)* Add encrypted keys and configuration for transifex
- *(build)* Prevent loop and remove release on develop
- *(cicleci)* Add circle-ci config script
- *(circleci)* Change android api on docker image
- *(circleci)* Preinstalled gradle
- *(circleci)* Add required folders for gradle
- *(release)* 2.1.3
- *(build)* Release **beta** for version 2.1.3-beta
- *(workflow)* Create workflow with build and test
- *(circleci)* Create a workflow
- *(circleci)* Add sequential job execution
- *(circleci)* Add sequential job execution
- *(circleci)* Refactor script
- *(circleci)* Rename script
- *(circleci)* Add docker information
- *(circleci)* Add version and code
- *(circleci)* Add steps
- *(circle)* Fix indentation
- *(circleci)* Setup enviroment
- *(circleci)* Add deploy to workflows
- *(circleci)* Add permission on setup script
- *(circleci)* Install tools
- *(circleci)* Install gem
- *(circleci)* Refactor python script
- *(circleci)* Fix script
- *(circleci)* Test imagen
- *(circleci)* Fix imagen
- *(circleci)* Install ruby
- *(circleci)* Install component
- *(circleci)* Change install folder
- *(circleci)* Add fastlane install
- *(circleci)* Update version and code
- *(circleci)* Remove jq install
- *(circleci)* Remove folder
- *(circleci)* Install with root
- *(circleci)* Refactor gradle to gradlew
- *(circleci)* Setup git config
- *(circleci)* Add start emulator script
- *(circleci)* Deploy on Google Play
- *(circleci)* Activate ruby and fastlane install
- *(fastlane)* Improve gem fastlane install
- *(circle)* Install fastlane with sudo
- *(circleci)* Remove params on fastlane install
- *(circleci)* Install complete fastlane
- *(circleci)* Move decrypt to setup
- *(circleci)* Update openssl file
- *(circleci)* Add encrypted file with new openssl version
- *(build)* Setup android build tool
- *(build)* Fix export var
- *(circleci)* Remove BUILD_TOOL from script add on site
- *(fastlane)* Setup fastlane env for telegram
- *(gradle)* Add factor number on version code to deploy
- *(changelog)* Add changelog script
- *(build)* Push android manifest to branch
- *(changelog)* Manage changelog file with header and push gh-page
- *(build)* Add permission to push
- *(build)* Add changelog from branch
- *(javadoc)* Add javadoc shell script
- *(circleci)* Add javadoc shell script to config
- *(circleci)* Comment google play deploy and changelog
- *(coverage)* Add coverage script
- *(build)* Comment version and code
- *(circleci)* Emulator start
- *(circleci)* Add launch emulator
- *(circleci)* Wait for emulator
- *(transifex)* Add transifex workflow
- *(circleci)* Refactor google play for fastlane script
- *(releases)* Add github releases script
- *(circleci)* Prepare config.yml for github releases
- *(workflow)* Add workflow
- *(circleci)* Update identation
- *(transifex)* Update locales files
- *(circleci)* Add instrumented test workflow
- *(circleci)* Add instrumented test api 26
- *(circleci)* Min android api available 16 max 23
- *(circleci)* Refactor android api max 25 and min 16 with arm
- *(javadoc)* Get report folder from branch
- *(circleci)* Just run documentation on workflow
- *(javadoc)* Get reports folder from branch
- *(circleci)* Add simulator for coverage report
- *(coverage)* Update coverage script
- *(coverage)* Clean git files
- *(coverage)* Replace folders with dot for design
- *(test)* Instrumented test
- *(circleci)* Add custom webhook notification
- *(circleci)* Remove update version and code commit
- *(transifex)* Remove transifex commit
- *(transifex)* Refactor pull and push
- *(circleci)* Add all items to workflows
- *(circleci)* Remove flyve_ prefix
- *(circleci)* Add filters to work flow
- *(travis)* Remove travis scripts
- *(circleci)* Change test job name for human readable
- *(test)* Update test name on workflows
- *(git)* Remove fastlane readme.md from gitignore
- *(header)* Add scripts to edit coverage and javadoc header
- *(javadoc)* Add header, replace files and add style
- *(coverage)* Add header, replace files and add style
- *(circleci)* Remove notify webhook
- *(coverage)* Change folder
- *(javadoc)* Fix git checkout
- *(changelog)* Add jekyll header
- *(changelog)* Add changelog script on production
- *(fastlane)* Skip documentation
- *(changelog)* Commit android manifest file
- *(releases)* Add preset angular
- *(build)* Update version
- *(release)* Update script to generate the release
- *(release)* Add install node github release
- *(release)* Upload zip
- *(release)* Add apk file
- *(version)* Add manisfest and changelog to git
- *(circleci)* Create assemble Release
- *(circleci)* Reorder production steps
- *(releases)* Rename the upload artifact
- *(screenshot)* Add initial setup
- *(screenshot)* Add screengrab job
- *(fastlane)* Add package on setup
- *(circleci)* Add screenshot on workflows
- *(screenshot)* Add screengrab job  (#47)
- *(about)* Create file with properties values
- *(circleci)* Add about step on beta and production jobs
- *(build)* Update manifest path
- *(about)* Add complete hash commit id for the link
- *(about)* Fix properties file name
- *(about)* Remove delete file
- *(about)* Take the previous commit
- *(about)* Update commit with circleci environment
- *(release)* Add conventional github releaser
- *(release)* Add /dev/null || true to solve exit 1
- *(about)* Add commit sliced
- *(release)* Add script to push changes
- *(circleci)* Add push changes step on deploy production job
- *(merge)* Merge back the develop branch
- *(validate)* Add validation script
- *(circleci)* Rename test name and remove filters
- *(circleci)* Refactor workflows
- *(push)* Push to develop merge with master
- *(css)* Improve script to add css and headers
- *(changelog)* Remove rewrite header
- *(circleci)* Move changelog to documentation jobs
- *(circleci)* Add update version and code on documentation jobs
- *(screenshot)* Add screenshot script
- *(circleci)* Add push step on screenshot jobs
- *(circleci)* Update validate script
- *(screenshot)* Update screenshot folder
- *(screenshot)* Add header on screenshot
- *(screenshot)* Add about information
- *(circleci)* Add cache on screenshot jobs
- *(licenses)* Accept android licenses
- *(screenshot)* Add all change on git
- *(circleci)* Accept licenses on build
- *(circleci)* Refactor accept licenses on build
- *(git)* Remove local tags
- *(screengrab)* Add accueil test
- *(circleci)* Change android emulator api
- *(circleci)* Restore api version 25
- *(release)* Generate CHANGELOG.md for version 0.1.0
- *(release)* Update version (0.1.0) and code number (931)
- *(release)* Generate CHANGELOG.md for version 0.2.0
- *(release)* Update version (0.2.0) and code number (932)
- *(release)* Generate CHANGELOG.md for version 0.3.0
- *(release)* Update version (0.3.0) and code number (939)
- *(release)* Generate CHANGELOG.md for version 0.4.0
- *(release)* Update version (0.4.0) and code number (942)
- *(release)* Generate CHANGELOG.md for version 0.5.0
- *(release)* Update version (0.5.0) and code number (949)
- *(circleci)* Add validate workflow on jobs
- *(circleci)* Update git develop merge
- *(git)* Add pull origin develop
- *(versioncode)* Update version code factor update
- *(circleci)* Fix case of ChangeLog
- *(circleci)* User name of git and other typos
- *(circleci)* Change repository slug
- *(git)* Avoid multiples push to the repo
- *(circleci)* Use BUILD_NUM as version code
- *(release)* Generate CHANGELOG.md for version 0.1.0
- *(release)* Update version (0.1.0) and code number (979)
- *(screenshot)* Update api level 25 emulator
- *(fastlane)* Update signer and zipalign
- *(circle)* Create apk with release
- *(fastlane)* Add release to the path
- *(circle)* Remove api 25
- *(fastlane)* Add log path
- *(fastlane)* Fix path
- *(fastlane)* Add release folder on zipalign
- *(fastlane)* Screenshot complete path
- *(fastlane)* Add root folder path
- *(fastlane)* Update test path
- *(transifex)* Add arabic language
- *(transifex)* Add Czech republic language
- *(transifex)* Add Hebrew language
- *(transifex)* Add Saudi Arabia language
- *(transifex)* Fix underscord character by - on values folders
- *(circle)* Change workflow
- *(circle)* Add update of properties values
- *(circle)* Add commands for changelog
- *(circle)* Update variables name
- *(circle)* Add command to find and upload the apk
- *(circle)* Rename folders for normalization
- *(circle)* Add commands to updateversion script
- *(circle)* Change command to update develop
- *(release)* Generate ChangeLog for version 1.1.0
- *(release)* Update version on android manifest
- *(release)* Generate ChangeLog for version 1.2.0
- *(release)* Update version on android manifest
- *(release)* Generate ChangeLog for version 1.3.0
- *(release)* Update version on android manifest
- *(circle)* Migrate to yarn
- *(circle)* Change workflow
- *(circle)* Add new scripts
- *(circle)* Update about script
- *(circle)* Delete unused scripts
- *(circle)* Fix path to apk
- *(circle)* Add commands to update version
- *(circle)* Add commands to screenshots
- *(fastlane)* Add deploy type
- *(release)* Fix the apk file
- *(push)* Improve the script with less code
- *(release)* Fix github token variable
- *(circleci)* Implement a new workflow with release
- *(script)* Add new scripts with the new workflow
- *(gems)* Add gemfile and gemfile.lock
- *(fastlane)* Add certification lane annd add skip_upload_aab true
- *(circleci)* Add update version code and name
- *(release)* Set the version release
- *(release)* Generate CHANGELOG.md for version 1.0.0-rc.1
- *(setup)* Install transifex on setup
- *(circle)* Add check transifex job
- *(package)* Update gh-pages to version 2.0.0
- *(package)* Update lockfile yarn.lock
- *(environment)* Upgrade urllib to transifex
- *(deploy)* Change config ci and deploy beta and production
- *(deploy)* Add documentation header and version code ci
- *(deploy)* Update config and release script
- *(deploy)* Update release script
- *(setup)* Install transifex on setup
- *(circle)* Add check transifex job
- *(package)* Update gh-pages to version 2.0.0
- *(package)* Update lockfile yarn.lock
- *(environment)* Upgrade urllib to transifex
- *(deploy)* Change config ci and deploy beta and production
- *(deploy)* Add documentation header and version code ci
- *(deploy)* Update config and release script
- *(deploy)* Update release script
- *(release)* Generate CHANGELOG.md for version 1.0.0-rc.2
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(transifex)* Add languages in transifex
- *(release)* Update version on android manifest
- *(transifex)* Add language basque in transifex
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(documentation)* Fix path to folder
- *(release)* Update version on android manifest
- *(documentation)* Add run in master
- *(release)* Update version on android manifest
- *(build)* Release 1.0.0
- *(enviroment)* Change name github enviroments
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(package)* Update standard-version to version 7.0.0
- *(package)* Update lockfile yarn.lock
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Update version on android manifest
- *(release)* Generate CHANGELOG.md for version 1.0.0

<!-- generated by git-cliff -->
