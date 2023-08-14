# Sample Weather App

## The Architecture
The architecture employed on this project MVVM (Model - View - ViewModel). MVVM combines the advantages of separation of concerns provided by MVP while leveraging the advantages of data bindings. The result is a pattern where the model drives as many operations as possible, minimizing the logic in the view, although in this case,  no  data binding was used.

## The User Interface
For the user interface, i opted to get hands on experience on the new proposed Jetpack Composable. Jetpack Compose is a new UI design toolkit for building native user interfaces or screen designs on android mobile applications using Kotlin code. It's almost similar to Flutter as it uses a single code base however, it is mainly meant for Android OS. This toolkit brings simplification and speed to UI designs when developing an app. You use fewer lines of code and reduce boilerplate code via its single code base architecture. To build an app in Jetpack Compose you need to know the Kotlin programming language and have the flexibility of integrating with intuitive Kotlin APIs for better app design.

## Builds
For this project, i decided to use gradle to manage build variants and signing. for the build variants within the projects, there is:- 
- Debug: This has the package postfixed by `.debug`, making it have a different package name from the other(s). The main reason behind this is to better manage different sandbox environments and to better track api usage metrics. Aside from that, it also serves as an internal staging environment. The debug version is signed using a different keystore
- Beta: This build type has a package postfixed by `.beta`. The aim for this build is to create a beta version of the application to the masses for further testing. It shares almost all credentials with the release build type
- Release: The release package is intended for deployment to the public. it is signed using a different keystore credentials. The package name does not have a prefix

### Generating Builds
For the different build types to be generated, the following files are required:
- secrets.properties:- Contains all the secret credentials for the project. It should be placed in the root directory
- sampleWeatherApp.keystore:- Contains the keystore credentials for the release and beta builds. Should be placed in the root directory
- sampleWeatherAppDebug.keystore:- Contains all the keystore credentials for debug build type. Should be placed in the root directory
- google-services-json: Firebase configuration file

### Third party libraries
- Network

`implementation "com.squareup.retrofit2:retrofit:$squareup_retrofit2_version"`

`implementation "com.squareup.retrofit2:converter-gson:$squareup_retrofit2_version"`

`implementation "com.squareup.okhttp3:okhttp:$squareup_okhttp3_version"`

`implementation "com.squareup.okhttp3:logging-interceptor:$squareup_okhttp3_version"`


- Location

`implementation 'com.google.android.gms:play-services-location:21.0.1'`

`implementation 'com.google.maps.android:maps-compose:2.12.0'`

`implementation 'com.google.android.gms:play-services-maps:18.1.0'`

`implementation 'com.google.android.libraries.places:places:3.0.0'`

- Deployment

`implementation 'com.google.firebase:firebase-appdistribution-api:16.0.0-beta08'`

`debugImplementation 'com.google.firebase:firebase-appdistribution:16.0.0-beta08'`

`betaImplementation 'com.google.firebase:firebase-appdistribution:16.0.0-beta08'`

## CI-CD
For CI-CD, we are using local testing and deployment in this case, FirebaseAppDistribution. Before you can use the gradle plugin, you must first authenticate with your firebase project. By default, the gradle plugin looks for the credentials form the Firebase CLI is no other authentication method is used. See <a href="https://firebase.google.com/docs/cli#sign-in-test-cli">Log in with the Firebase</a> CLI for instructions on how to authenticate your project.
We generate the change log file from previous git commits for easier deployment
- For Debug `./gradlew assembleDebug appDistributionUploadDebug`
- For Beta `./gradlew assembleDebug appDistributionUploadBeta`
- For Release `./gradlew assembleDebug appDistributionUpload`

Alternatively, run the .sh files in the root dir to deploy