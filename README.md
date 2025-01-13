# LastCrash Android (Kotlin) Sample Application

## API Documentation

[LastCrash Android Latest API Documentation](https://docs.lastcrash.io/android/api/latest/index.html)

## SDK Setup Instructions

### Add Dependencies:

- In your project settings.gradle.kts file, add the following under **dependencyResolutionManagement-> repositories:**

```bash
dependencyResolutionManagement {
	...
  repositories {
    ...
    maven("https://mvn.lastcrash.io/releases")
  }
}
```

- Then, add the following to your app build.gradle.kts file, then sync your project:

```bash
implementation("io.lastcrash:lastcrash-android:2.0.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
```

In the Android manifest for you app ensure the following permissions are granted:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

### Initialize SDK:

- In your MainActivity file, add `LastCrashReportSenderListener`, initialize the SDK, and configure the `lastCrashReportSenderHandleCrash` method.
- Replace `LASTCRASH_API_KEY` with your LastCrash API key.

### Optional Listener

Setting the listener is optional.  If you would like to control the logic behind sending crash reports then implement the `LastCrashReportSenderListener` interface and call `setCrashReportSenderListener`.

The `lastCrashReportSenderHandleCrash` method will be called when crash reports are available to send.  This allows you to implement your own logic or ask the user for permission to send crash reports.

`LastCrash.sendCrashes()` must be called to send the crash reports if the delegate is used.

### Application not responding support

A call to `LastCrash.applicationInitialized()` must be made after your app is initialized in order to track application not responding (ANR) errors.

The reason this call to `LastCrash.applicationInitialized()` is required is to starting ANR monitoring only after everything in your app is initialized/loaded so false positives can be avoided.

### Freeze support

A call to `LastCrash.applicationInitialized()` must be made after your app is initialized in order to track freeze (application not responding or ANR) errors.  

The reason this call to `LastCrash.applicationInitialized()` is required is to starting Freeze monitoring only after everything in your app is initialized/loaded so false positives can be avoided.

### Masking support

All text that isn't part of the app's localization files will be redacted on device to prevent any user or customer PII from being captured.  Ensure that all user interface elements are utilizing localization strings to get the most value out of the recorded crash videos.

### Jetpack Compose masking

To masking a view with Jetpack compose add the following as a modifier on the view:

```kotlin
Modifier.onGloballyPositioned { coordinates ->
    val size = coordinates.size
    val position = coordinates.positionInRoot()
    val maskRect = Rect(position.x.toInt(), position.y.toInt(), (position.x+size.width).toInt(), (position.y + size.height).toInt())
    LastCrash.addMaskRect("masked_view", maskRect)
}
```

Then remember to remove the mask rect in the `onDestroy` lifecycle method of the containing `Activity` or `Fragment`.

```kotlin
LastCrash.removeMaskRect("masked_view")
```

### View based masking

Views can be explicitly masked by passing a View object reference or by view id.  An important note: it is your responsibility to manage the masked view lifecycle to add and remove masked views as they are shown on the screen.

A best practice is to add masked views in `onResume` methods of an `Activity` or `Fragment`.

```kotlin
// Mask view by object reference
LastCrash.addMaskView(view)

// Mask view by id
LastCrash.addMaskViewId(R.id.masked_view)
```

Masked views should be removed in the `onPause` method of an `Activity` or `Fragment`.

```kotlin
// Remove mask view by object reference
LastCrash.removeMaskView(view)

// Remove mask view by id
LastCrash.removeMaskViewId(R.id.masked_view)
```

### Rectangle based masking

Sections of the screen can be masked by rectangles relative to the app's container view frame.  An important note: it is your responsibility to manage the masked rect  lifecycle to add and remove masked rects.

A best practice is to add masked rects in `onResume` methods of an `Activity` or `Fragment`.

```kotlin
// Mask view by rect
LastCrash.addMaskRect("masked_rect", Rect(0,0,100,100))
```

Masked rects should be removed in the `onPause` method of an `Activity` or `Fragment`.

```kotlin
// Remove mask rect
LastCrash.removeMaskRect("masked_rect")
```

### Networking support

A `LastCrashInterceptor` class must be added to the OkHttpClient before its built to track networking errors and get summarized networking statistics including bytes sent/recieveed and response time.

```java
OkHttpClient.Builder builder = new OkHttpClient.Builder();
builder.addInterceptor(new LastCrashInterceptor());
OkHttpClient client = builder.build();
```

### Force termination detection

Add the following service to your application manifest within the `application` tag in order to properly track user force terminations:

```xml
    <service android:name="io.lastcrash.sdk.ForceTerminationService"/>
```

Look at the application manifest in this repo for reference.

### Kotlin

```kotlin
class MainActivity : LastCrashReportSenderListener {
  ...
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ...
    LastCrash.setCrashReportSenderListener(this)
    LastCrash.configure("LASTCRASH_API_KEY", this)
    LastCrash.applicationInitialized()
  }
  ...
  override fun lastCrashReportSenderHandleCrash() {
    // logic here to handle crash
    LastCrash.sendCrashes()
  }
}
```

## ProGuard and Native Symbols

LastCrash can deobfuscate with any ProGuard-compatible mapping file and native (NDK) symbols file.

If your app uses ProGuard add the following to the ProGuard file:

```
-keepattributes SourceFile,LineNumberTable        # Keep file names and line numbers.
-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.
```

### Gradle

Ensure `pluginManagement` includes the LastCrash maven repository.

```groovy
pluginManagement {
    repositories {
        maven {
            url "https://mvn.lastcrash.io/releases"
        }
        //...
    }
}
```

Add the `io.lastcrash.gradle` plugin to your gradle environment and configure the LastCrash plugin with your API Key.

Configure which build variants you would like to deobfuscate and upload ProGuard mappings to LastCrash.

Here are full examples for both Kotlin and Groovy app gradle scripts.

#### Kotlin: build.gradle.kts

```kotlin
import io.lastrash.gradle.LastCrashExtension

plugins {
    // ...
    id("io.lastcrash.gradle") version "1.0"
}

lastcrash {
    apiKey = "LASTCRASH_API_KEY"
}
// ...

android {
  // To enable LastCrash mapping file upload for specific build types:
  buildTypes {
    release {
      minifyEnabled = true
      configure<LastCrashExtension> {
        uploadSymbols = true
      }
    }
  }

  ...

  // To enable LastCrash mapping file upload for specific product flavors:
  flavorDimensions += "environment"
  productFlavors {
    create("staging") {
      dimension = "environment"
      ...
      configure<LastCrashExtension> {
        uploadSymbols = false
      }
    }
    create("prod") {
      dimension = "environment"
      ...
      configure<LastCrashExtension> {
        uploadSymbols = true
      }
    }
  }
}
```

#### Groovy: build.gradle

```groovy
plugins {
    id("io.lastcrash.gradle") version "1.0"
}

lastcrash {
    apiKey = "LASTCRASH_API_KEY"
}

android {
  // To enable LastCrash mapping file upload for specific build types:
  buildTypes {
    release {
      minifyEnabled true
      lastcrashExtension {
        uploadSymbols true
      }
    }
  }

  ...

  // To enable Crashlytics mapping file upload for specific product flavors:
  flavorDimensions "environment"
  productFlavors {
    staging {
      dimension "environment"
      ...
      lastcrashExtension {
        uploadSymbols false
      }
    }
    prod {
      dimension "environment"
      ...
      lastcrashExtension {
        uploadSymbols true
      }
    }
  }
}
```

## Testing

Run app in `Release` mode with debugging turned off. For best results, run on a physical device, rather than an emulator.

Tap `Test Crash` to trigger a crash.  Then re-run the app and watch the output log for the crash being uploaded.  Go to your LastCrash account to view the crash recording.
