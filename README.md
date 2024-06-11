# LastCrash Android (Kotlin) Sample Application

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
implementation("io.lastcrash:lastcrash-android:1.1.18")
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

- In your MainActivity file, add `LastCrashListener`, initialize the SDK, and configure the `lastCrashDidCrash` method.
- Replace `LASTCRASH_API_KEY` with your LastCrash API key.

### Optional Listener

Setting the listener is optional.  If you would like to control the logic behind sending crash reports then implement the `LastCrashListener` interface and call `setListener`.

The `lastCrashDidCrash` method will be called when crash reports are available to send.  This allows you to implement your own logic or ask the user for permission to send crash reports.

`LastCrash.send()` must be called to send the crash reports if the delegate is used.

### Application not responding support

A call to `LastCrash.applicationInitialized()` must be made after your app is initialized in order to track application not responding (ANR) errors.

The reason this call to `LastCrash.applicationInitialized()` is required is to starting ANR monitoring only after everything in your app is initialized/loaded so false positives can be avoided.

### Freeze support

A call to `LastCrash.applicationInitialized()` must be made after your app is initialized in order to track freeze (application not responding or ANR) errors.  

The reason this call to `LastCrash.applicationInitialized()` is required is to starting Freeze monitoring only after everything in your app is initialized/loaded so false positives can be avoided.

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
class MainActivity : LastCrashListener {
  ...
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ...
    LastCrash.setListener(this)
    LastCrash.configure("LASTCRASH_API_KEY", this)
    LastCrash.applicationInitialized()
  }
  ...
  override fun lastCrashDidCrash() {
    // logic here to handle crash
    LastCrash.send()
  }
}
```

## Testing

Run app in `Release` mode with debugging turned off. For best results, run on a physical device, rather than an emulator.

Tap `Test Crash` to trigger a crash.  Then re-run the app and watch the output log for the crash being uploaded.  Go to your LastCrash account to view the crash recording.
