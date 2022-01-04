 <h1 align="center">Huawei-Exoplayer Adapter Github Documentation</h3>

 ![Latest Version](https://img.shields.io/badge/latestVersion-1.0.0-yellow) ![Java](https://img.shields.io/badge/language-java-blue)
<br>
![Supported Platforms](https://img.shields.io/badge/Supported_Platforms:-Native_Android_-orange)

# Introduction

In this documentation we explained how to use Huawei-Exoplayer adapter in android.


Click [here](https://medium.com/huawei-developers/how-to-use-huawei-roll-ads-with-exoplayer-34ecbe8a5b86) to check the medium post about this adapter.

# How to start?
  
## Create an ad unit on Huawei Publisher Service

1. Sign in to [Huawei Developer Console](https://developer.huawei.com/consumer/en/console) and create an ad unit for Roll Ads.

## What is Exoplayer?

Exoplayer is an extensible media player for Android developed by Google.
Click [here](https://github.com/google/ExoPlayer) to check the offical Github page of the exoplayer.

## What is Roll Ads?

Roll ads are displayed as short videos or images, before, during, or after the video content is played.
Click [here](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/publisher-service-instream-0000001058253743) to learn more about roll ads.



## Integrate the Huawei Exoplayer Adapter SDK

In the **project-level** build.gradle, include Huawei's Maven repository.

```groovy
repositories {
    google()
    jcenter() // Also, make sure jcenter() is included
    maven { url 'https://developer.huawei.com/repo/' } // Add this line
    maven {url "https://jitpack.io"} // Add this line
}

...

allprojects {
    repositories {
        google()
        jcenter() // Also, make sure jcenter() is included
        maven { url 'https://developer.huawei.com/repo/' } //Add this line
        maven {url "https://jitpack.io"} // Add this line
    }
}
```
<h1 id="app-level"></h1>

In the app-level build.gradle, include following dependencies and enable multidex for the project :

```groovy
dependencies {
    ...
   implementation 'com.github.Explore-In-HMS:huawei.ads.exoplayer_adapter:v1.0.0'
   implementation 'com.google.android.exoplayer:exoplayer:<any version>'
   implementation 'androidx.multidex:multidex:2.0.1'
   implementation 'com.huawei.hms:ads:3.4.49.301' 
}
defaultConfig {
    multiDexEnabled true   // enable multidex for the project
}
```

# Version Change History

## 1.0.0


# How it works?

Adapter implementation code as follows. 


```java

  new HwExoPlayerAdapter.Builder(Context context, PlayerView playerview, "Huawei Roll ad unit id")
                .setAdItem(Placement.PREROLL, true)
                .setAdItem(Placement.FIRST_QUARTILE, false)
                .setAdItem(Placement.MIDDLE, true)
                .setAdItem(Placement.THIRD_QUARTILE, false)
                .setAdItem(Placement.POSTROLL, true)
          
               //optional, used for customize the ad requests
          
                .setAppLanguage("en")              
                .setChildProtectionCOPPA(true)   
                .setTcfConsent("")                
                .setGender(Gender.MALE)        
                .setOnlyNonPersonalized(true)    
                .setUnderOfAgeConsent(true)      
                .setLocation(Location location)  
                .useLocationForRequest(true) 
                .build();

```




> **_NOTE:_** adapter must be build  <b>before</b> <i>SimpleExoPlayer.prepare()</i> method called.
<h1></h1>

# Adapter Structure

## Constructor Parameters

 When creating the builder 3 parameters must be given ; context, PlayerView instance, and  Roll ad unit id.
 
## Set Ad item method

This method is used to specify the video ads according to the placement and skippable value.

### Placement

For the placement, There are 5 placements you can choose (Preroll, FirstQuartile, Middle, ThidQuartile, and Post-roll). 
According to these placements, the ad will pop up in the video content.
For example, 2 Ad items are created and their placements are middle and post-roll.
In the middle and at the end of your video content, ads will be shown.

> **_NOTE:_** Only one ad will be shown for each placement. Ad pods are not currently supported .

### Skip Value 

Used to decide if the ad is skippable or not. Give <b>true</b> value for skippable ads.

## Optional Methods

### setAppLanguage()

Sets the language in which an ad needs to be returned for an app.

### setChildProtectionCOPPA()

Sets whether to process ad requests according to the COPPA.  Use value <b>true</b> to allow it.

### setTcfConsent()

Sets the user consent string that complies with TCF v2.0.

### setGender()

Sets the gender. use Huawei Gender Constant class  to set it.  (e.g. Gender.MALE)

### setOnlyNonPersonalized()

Sets whether to request non-personalized ads. Use value <b>true</b> to allow only non-personalized ads.

### setUnderOfAgeConsent()

Sets whether to process ad requests as directed to users under the age of consent. Use value <b>true</b> to allow it.

### setLocation()

Sets the location information passed by your app.

### useLocationForRequest()

Sets whether to carry the location information in an ad request. By default, the information is carried.

