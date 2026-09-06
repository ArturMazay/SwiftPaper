# Firebase setup (optional for v1.0)

SwiftPaper builds **without** `google-services.json`. Analytics/Crashlytics can be added before soft launch.

## Steps

1. Create a Firebase project and register Android app with applicationId `com.swiftpaper.app`.
2. Download `google-services.json` into `app/`.
3. In root `build.gradle.kts` add:
   ```kotlin
   plugins {
       id("com.google.gms.google-services") version "4.4.2" apply false
       id("com.google.firebase.crashlytics") version "3.0.3" apply false
   }
   ```
4. In `app/build.gradle.kts` apply plugins and add:
   ```kotlin
   implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
   implementation("com.google.firebase:firebase-analytics-ktx")
   implementation("com.google.firebase:firebase-crashlytics-ktx")
   ```
5. Events to log: `export_completed`, `ad_interstitial_shown`, `ad_rewarded_earned`, `pro_purchase`.

Until then, use Logcat + Play Console pre-launch report during closed testing.
