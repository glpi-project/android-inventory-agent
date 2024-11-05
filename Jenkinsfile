def GRADLE_BUILD

// Load Jenkins shared library
@Library('utils') _

pipeline {
    agent any

    environment {
        SHORT_COMMIT = GIT_COMMIT.substring(0, 8)
        GIT_USER = getUserCommit()
        // Define build tools version
        BUILD_TOOLS = "33.0.0"
    }

    options {
        // Number of build to keep for each branch
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Concurrent builds are not allowed on the same branch, only one build at the same time
        disableConcurrentBuilds()
    }

    stages {
        stage('Init') {
            steps {
                loadEnv project: 'ubisolutions-geodis-glpiclient-mobile-java', platform: 'android'

		// Copy $KEYSTORE_PROPERTIES from Jenkins
                sh'''
                            set +x
                    cp $KEYSTORE_PROPERTIES .
                            set -x
                        '''
                
                script {
                    // GRADLE_BUILD is used to retrieve and keep the version name
<<<<<<< HEAD
                    GRADLE_BUILD = sh(script: "sh ./gradlew -q printVersion", returnStdout: true).trim()
=======
                    GRADLE_BUILD = sh(script: "cd src && sh ./gradlew -q printVersion", returnStdout: true).trim()
>>>>>>> Create Jenkinsfile
                    echo GRADLE_BUILD
                }
                // Initialize artifactory server
                rtServer(id: JFROG_SERVER_ID, url: JFROG_URL, credentialsId: JFROG_CREDENTIALS_ID)
            }
        }

        stage('DeliveryDebug') {
            when {
                anyOf {
                    branch 'develop'
                }
            }

            environment {
                APK_DEBUG_DIR = "app/build/outputs/apk/debug"
<<<<<<< HEAD
                APK_DEBUG_NAME = "geodis-glpiclient-$BUILD_NUMBER-$GRADLE_BUILD-Debug"
                JFROG_FOLDER = "$JFROG_REPOSITORY/$BRANCH_NAME/$GRADLE_BUILD/"
=======
                APK_DEBUG_NAME = "geodis-glpiclient-$BUILD_NUMBER-Debug"
>>>>>>> Create Jenkinsfile
            }

            steps {
                sh'''
<<<<<<< HEAD
=======
                    cd src
>>>>>>> Create Jenkinsfile
                    sh ./gradlew assembleDebug
                    mv $APK_DEBUG_DIR/app-debug.apk $APK_DEBUG_DIR/$APK_DEBUG_NAME.apk
                '''
            }
            
            post {
                success {
                    rtUpload (
                        serverId: "$JFROG_SERVER_ID",
                        spec: """{
                            "files": [
                            {
                                    "pattern": "$APK_DEBUG_DIR/*.apk",
                                    "target": "$JFROG_FOLDER"
                                }
                            ]
                        }"""
                    )
                }
            }
        }

        stage('DeliveryRelease') {
            when {
                anyOf {
                    branch 'main'
                }
            }

            environment {
<<<<<<< HEAD
                APK_RELEASE_DIR = "app/build/outputs/apk/release"
=======
                APK_RELEASE_DIR = "app/release"
>>>>>>> Create Jenkinsfile
                APK_RELEASE_NAME = "geodis-glpiclient-$BUILD_NUMBER-$GRADLE_BUILD-Release"
                JFROG_FOLDER = "$JFROG_REPOSITORY/$BRANCH_NAME/$GRADLE_BUILD/"
            }

            steps {
                sh'''
<<<<<<< HEAD
                    sh ./gradlew assembleRelease
                    ls -R
                    mv $APK_RELEASE_DIR/app-release.apk $APK_RELEASE_DIR/$APK_RELEASE_NAME.apk
=======
                    cd src
                    sh ./gradlew assembleRelease
                    mv $APK_RELEASE_DIR/geopics-2.2.apk $APK_RELEASE_DIR/$APK_RELEASE_NAME.apk
>>>>>>> Create Jenkinsfile
                '''
            }
            
            post {
                success {
                    rtUpload (
                        serverId: "$JFROG_SERVER_ID",
                        spec: """{
                            "files": [
                            {
<<<<<<< HEAD
                                    "pattern": "$APK_RELEASE_DIR/*.apk",
=======
                                    "pattern": "src/$APK_RELEASE_DIR/*.apk",
>>>>>>> Create Jenkinsfile
                                    "target": "$JFROG_FOLDER"
                                }
                            ]
                        }"""
                    )
                }
            }
        }

    }
}
