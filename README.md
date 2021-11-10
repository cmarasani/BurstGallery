# Project BurstGallery for Android

BurstGallery is a WebView based project to help android developers understand how to upload and process images/videos from a WebView.
This project helps android developers to solve below problems:
1. Embed Burst in a WebView with in the app instead of opening it in a browser window, including the media capture/import process,
2. Prompt popup to ask for permissions needed to capture or import media,
3. Prompting mailing applications on clicking any 'mailto' link

## Getting Started
This project contains android application build files, that you can download and use directly with latest Android Studio. Follow further steps to get started in your personalised environment.

### Requirements
Minimum Android API 16+ (4.1 JellyBean) SDK. You can use any IDE of your choice but to be specific, I used latest Android Studio 2020.3.1 last updated on July 27th, 2021 with updated SDKs and Builds.

### Test Run
Just putting these basic steps to help starters:
1. `File > Open` choose project folder and let android studio download supportive libraries.
2. `Build > Clean Project` and `Build > Rebuild Project`
3. If everything goes alright, you can start testing
4. In case or any error, check Build and Logcat tab for details

### Permissions needed in AndroidManifest.xml
Followings are permissions required for all the mentioned features to work.
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```
`INTERNET` permission is required to access online content,
`WRITE_EXTERNAL_STORAGE`+`CAMERA` permissions to take photo from camera and save it to gallery,
`READ_EXTERNAL_STORAGE` permission to access photo or video from gallery,
`SYSTEM_ALERT_WINDOW` permission to show permission overlay.

### Custom WebView
BurstWebView.java is a custom WebView which is extended from WebView and has necessary changes to take care of prompting the permission popup for camera and media, and opening of file chooser.
Note to developer: Update package and import of BurstWebViewClient.java in BurstWebView.java and leave rest of the file as is.

### Custom WebViewClient
BurstWebViewClient.java is a custom WebViewClient which is extended from WebViewClient and has necessary changes to take care of prompting mailing app list on clicking 'mailto' link.
Note to developer: Update package in BurstWebViewClient.java and leave rest of the file as is.

### What else
Developer should update the Burst Embed WebView reference to BurstWebView in their layout and activity files.
#### Example change in layout file:
from
WebView
to
com.example.burstgallery.BurstWebView
#### Example change in activity file:
from
private WebView webView;
webView = = (WebView) findViewById(R.id.burstWebview);
to
private BurstWebView webView;
webView = (BurstWebView) findViewById(R.id.burstWebview);
#### Check MainActivity.java for more detailed changes that are needed in the onCreate and onActivityResult functions.
#### Check @todo statements in the below files where all the changes needed are highlighted
1. BurstWebView.java
2. BurstWebViewClient.java
3. AndroidManifest.xml
4. activity_main.xml
5. MainActivity.java

## About Project
This project is developed by team at Burst.

**PROJECT NOTE:** Android 4.4 doesn't support WebView upload default method and it's a permanent bug as no more KitKat updates are going to be made. All other versions are working fine.