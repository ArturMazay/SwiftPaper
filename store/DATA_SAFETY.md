# Play Console — Data safety form checklist (SwiftPaper)

Use this as a fill-in guide for **App content → Data safety**. Align answers with the live build (Yandex Mobile Ads + local file processing + optional Play Billing). Re-check after any SDK change.

**Package:** `com.swiftpaper.app`  
**Privacy policy URL:** `https://swiftpaper.app/privacy` (must be live HTTPS)

---

## Overview answers

| Question | Suggested answer |
|----------|------------------|
| Does your app collect or share any of the required user data types? | **Yes** (because of Yandex Mobile Ads / advertising identifiers & device data) |
| Is all user data encrypted in transit? | **Yes** (HTTPS for ad/network traffic; follow SDK defaults) |
| Do you provide a way for users to request deletion? | **Yes** — explain: no account; users clear app data / uninstall; documents never uploaded to developer servers. For ad data, point to Yandex / device ad settings |
| Does your app use account creation? | **No** |
| Does your app allow users to create an account? | **No** |

---

## Data collection & sharing (by category)

Mark **Collected** and/or **Shared** as below. “Shared” = sent to third parties (Yandex). “Collected” = obtained on device / via SDK even if only for ads.

### Likely **Yes — collected & shared** (via Yandex Mobile Ads)

| Data type | Collected | Shared | Ephemeral? | Required / Optional | Purpose |
|-----------|-----------|--------|------------|---------------------|---------|
| Device or other IDs (Advertising ID) | Yes | Yes | No | Optional* for core PDF features; required for free ad-supported experience | Advertising |
| App activity (e.g. ad interactions / page views if SDK reports) | Yes (SDK) | Yes | No | Optional* | Advertising |
| App info and performance (crash/diagnostics if SDK collects) | Yes if SDK does | Yes if shared | No | Optional | Advertising / Analytics (only if applicable) |
| Device or other IDs | Yes | Yes | No | Optional* | Advertising |
| Approximate location (IP-based, if declared by Yandex) | Yes if SDK | Yes if SDK | No | Optional | Advertising |

\*In Data safety UX, “Optional” vs “Required” usually means whether the user can use the app’s primary purpose without that data. Core tools work without ads if Pro; free users need network/ads stack. Prefer **Optional** + note that ads fund the free tier, or follow Play’s latest wording for ad-supported apps.

### Likely **No** (developer does not collect)

| Data type | Answer | Notes |
|-----------|--------|-------|
| Name, email, phone, address | No | No account / no profile |
| User payment info | No (handled by Google) | Declare per Play guidance for Billing; typically Google collects payment info, not the developer |
| Photos and videos | **Collected? On device only** | Users pick/capture media for PDF features. If data **never leaves the device** to you or a third party for processing, many teams answer **No** for “Photos and videos” under *collected by the app for transmission*. Confirm current Play definitions: if “collected” includes on-device processing only, follow the console help text. **Do not** mark as shared with Yandex unless the ad SDK accesses the photo library (it should not). |
| Files and docs | Same as photos | Local PDF/image processing only; not uploaded to SwiftPaper servers |
| Audio, health, financial info, etc. | No | |

**Recommended stance for photos/files:**  
In the form narrative / privacy policy: files processed on device, not sent to developer. In Data safety, only declare photo/file data types if Play’s definition treats on-device access as “collection.” When in doubt, declare **Photos and videos** / **Files and docs** as **Collected**, **not shared**, purpose **App functionality**, ephemeral/deleted when user clears files — and state users can deny permission.

---

## Data usage purposes (for declared ad-related data)

Select:

- [x] Advertising or marketing  
- [ ] Fraud prevention, security, compliance (only if SDK/Play requires)  
- [ ] Analytics (only if you add analytics SDK)  
- [ ] App functionality (for on-device file access, if declared)  
- [ ] Developer communications (no)

---

## Third parties

| Party | Role | Data |
|-------|------|------|
| Yandex (Mobile Ads) | Ad serving / mediation | Advertising ID, device/tech data, ad events |
| Google Play | Distribution, Billing, Vitals | Purchase tokens; optional vitals |

List Yandex in “Data shared with third parties” where the form asks.

---

## Security practices

- [x] Data is encrypted in transit (TLS)
- [ ] Users can request deletion of data (describe: uninstall / clear storage; no cloud account data held by developer)
- [ ] Independent security review (No, unless you obtain one)

---

## Families / children

- Target audience: **not** primarily children  
- Not designed for children under 13  
- Ads: ensure Yandex / ad settings comply with Designed for Families if you ever enroll (default: **do not** enroll in Families for this utility)

---

## Preview / store listing consistency

Before submitting Data safety:

1. Privacy policy live and matches this checklist (`store/PRIVACY_POLICY.md`).
- 2. Ads SDK present in release APK/AAB (`yandex-mobileads`).
3. Pro removes ads — do not claim “no ads data ever” while free build ships Yandex.
4. No analytics SDK unless also declared.
5. Camera / photos permissions match declared purposes.

---

## Quick copy-paste for “Data deletion”

```
SwiftPaper does not create user accounts and does not upload your documents to our servers.
To remove on-device data, clear the app’s storage in Android settings or uninstall the app.
Advertising data processed by Yandex Mobile Ads is subject to Yandex’s privacy policy and your device advertising settings.
Contact: privacy@swiftpaper.app
```

---

## Pre-submit verification

- [ ] Compared declarations to `app/build.gradle.kts` dependencies  
- [ ] Compared to `AdsManager` / `YandexBannerAd` (banner, interstitial, rewarded)  
- [ ] Confirmed no login / backend API for PDFs  
- [ ] Privacy URL returns 200 over HTTPS  
- [ ] Contact email placeholder replaced with a monitored inbox  
