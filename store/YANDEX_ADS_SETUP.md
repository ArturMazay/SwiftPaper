# Yandex Ads setup — replace demo unit IDs

SwiftPaper reads ad unit IDs from **BuildConfig** fields in `app/build.gradle.kts`. `YandexAdsConfig` and the ad UI/managers use those values.

**Do not ship Play builds with `demo-*-yandex` IDs.**

---

## Current (demo) values

In `app/build.gradle.kts` → `defaultConfig`:

```kotlin
buildConfigField("String", "YANDEX_BANNER_AD_UNIT_ID", "\"R-M-19995461-1\"")
buildConfigField("String", "YANDEX_INTERSTITIAL_AD_UNIT_ID", "\"R-M-19995461-2\"")
buildConfigField("String", "YANDEX_REWARDED_AD_UNIT_ID", "\"R-M-19995461-3\"")
buildConfigField("String", "YANDEX_APP_OPEN_AD_UNIT_ID", "\"R-M-19995461-4\"")
```


Wired in code:

| BuildConfig field | Used by |
|-------------------|---------|
| `YANDEX_BANNER_AD_UNIT_ID` | `YandexAdsConfig.BANNER_AD_UNIT_ID` → `YandexBannerAd` (Home) |
| `YANDEX_INTERSTITIAL_AD_UNIT_ID` | `YandexAdsConfig` → `AdsManager` interstitial |
| `YANDEX_REWARDED_AD_UNIT_ID` | `YandexAdsConfig` → `AdsManager` rewarded (HD / no watermark) |
| `YANDEX_APP_OPEN_AD_UNIT_ID` | `YandexAdsConfig` → `AdsManager` App Open (вход в приложение) |

---

## 1. Create units in Yandex Advertising Network

1. Open [Yandex Advertising Network](https://partner.yandex.com/) (Mobile Ads / apps).
2. Add application:
   - Platform: **Android**
   - Package name: **`com.swiftpaper.app`**
3. Create four ad units (names are for your cabinet only):

| Unit type | Suggested name | Maps to BuildConfig |
|-----------|----------------|---------------------|
| Banner | SwiftPaper Banner | `YANDEX_BANNER_AD_UNIT_ID` |
| Interstitial | SwiftPaper Interstitial | `YANDEX_INTERSTITIAL_AD_UNIT_ID` |
| Rewarded | SwiftPaper Rewarded | `YANDEX_REWARDED_AD_UNIT_ID` |
| App Open | SwiftPaper App Open | `YANDEX_APP_OPEN_AD_UNIT_ID` |

4. Copy each **Ad unit ID** string from the cabinet (not the demo IDs).

---

## 2. Replace IDs in Gradle

Edit `app/build.gradle.kts` and paste your real IDs (keep the escaped quotes):

```kotlin
buildConfigField("String", "YANDEX_BANNER_AD_UNIT_ID", "\"R-M-XXXXXX-Y\"")
buildConfigField("String", "YANDEX_INTERSTITIAL_AD_UNIT_ID", "\"R-M-XXXXXX-Y\"")
buildConfigField("String", "YANDEX_REWARDED_AD_UNIT_ID", "\"R-M-XXXXXX-Y\"")
buildConfigField("String", "YANDEX_APP_OPEN_AD_UNIT_ID", "\"R-M-XXXXXX-Y\"")
```

Replace `R-M-XXXXXX-Y` with the exact IDs from Yandex (format may look like `R-M-123456-7` — use whatever the cabinet shows).

**Optional (recommended):** use `local.properties` or CI secrets so real IDs are not committed:

```kotlin
// Example pattern — only if you add this yourself
val yandexBanner = providers.gradleProperty("YANDEX_BANNER_AD_UNIT_ID")
    .orElse("demo-banner-yandex")
// ... buildConfigField from property
```

For soft launch, direct BuildConfig replacement is fine if the repo is private.

Sync Gradle / rebuild so `BuildConfig` regenerates.

---

## 3. Verify in app

1. **Uninstall** any old install that might cache odd states; install a **release** or internal testing build.
2. Cold start → Home: **banner** loads (non-Pro).
3. Trigger flows that show **interstitial** (respect cooldown in `YandexAdsConfig` / `AdsManager`).
4. Trigger **rewarded** path (“Watch ad for HD / no watermark”).
5. Unlock **Pro** (or mock Pro prefs in debug): confirm ads **do not** show.
6. Check Logcat for Yandex init / load errors (`AdsManager` tag).

---

## 4. Checklist before Play upload

- [ ] All three BuildConfig values ≠ `demo-banner-yandex` / `demo-interstitial-yandex` / `demo-rewarded-yandex`
- [ ] Package name in Yandex cabinet matches `com.swiftpaper.app`
- [ ] Release AAB built after Gradle sync
- [ ] Privacy policy + Data safety mention Yandex Mobile Ads
- [ ] Publisher payout details started in Yandex (see `SOFT_LAUNCH.md`)
- [ ] Pro still removes ads

---

## 5. Rollback

To return to demos for local UI work only:

```kotlin
buildConfigField("String", "YANDEX_BANNER_AD_UNIT_ID", "\"demo-banner-yandex\"")
buildConfigField("String", "YANDEX_INTERSTITIAL_AD_UNIT_ID", "\"demo-interstitial-yandex\"")
buildConfigField("String", "YANDEX_REWARDED_AD_UNIT_ID", "\"demo-rewarded-yandex\"")
```

Never upload that configuration to a public testing or production track.
