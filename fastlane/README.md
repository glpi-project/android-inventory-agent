# Fastlane

Fastlane is a tool in ruby to release your iOS and Android app.

More info:
<https://fastlane.tools/>

In this android project we used to:

- Deploy to Google play beta
- Deploy to Google play production
- Send message to telegram with success or fail

## How to use

In Appfile is the configuration with package name and json key file

In Fastfile has our function:

- Beta
- Playstore
- and send to telegram

### In the folder actions we add custom actions

- To sign the apk
- To zip and align the apk
- To send message to telegram