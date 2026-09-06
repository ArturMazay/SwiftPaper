# Firebase (optional)

SwiftPaper builds without Firebase. Analytics / Crashlytics are not wired yet.

To enable later:

1. Create a Firebase project and register Android app id `com.swiftpaper.app`.
2. Download `google-services.json` into `app/`.
3. Apply the Google Services Gradle plugin and add the desired Firebase dependencies.
4. Until then, leave Firebase out of the release checklist so missing `google-services.json` does not block CI or local builds.
