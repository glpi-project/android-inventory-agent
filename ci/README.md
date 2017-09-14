# Continuous Integration script and files 

Here is placed files and bash script required to build, test and deploy the app

## Files description:

- release.keystore is the key to certified the app to deploy required by fastlane
- gplay.json.enc is the file with Google Play configuration required by fastlane

## Workflow description:

#### On feature branch

- run Build

#### On develop

- Setup environment (ci_setup.sh)
- Update version and code (ci_updateversion.sh)
- Transifex (ci_transifex.sh)
- Deploy to Google Play (ci_fastlane.sh)
- Create Coverage (ci_coverage)
- Create Javadoc (ci_javadoc)

#### On master

- Update version and code (ci_updateversion.sh)
- Deploy to Google Play (ci_fastlane.sh)
- Create a github release (ci_github_release.sh)
