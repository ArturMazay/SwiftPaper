# Privacy Policy — SwiftPaper

**Effective date:** September 6, 2026  
**App:** SwiftPaper (`com.swiftpaper.app`)  
**Contact:** privacy@swiftpaper.app *(placeholder — replace before publishing)*

This Privacy Policy describes how SwiftPaper (“we”, “us”, “the App”) handles information when you use the Android application.

---

## 1. Summary

- **No account.** You do not need to register or sign in to use SwiftPaper.
- **Documents stay on your device.** Scanning, image-to-PDF, PDF compression, and PDF merge run **locally on your phone**. We do not operate a server that receives your PDFs or photos for processing.
- **Ads.** The free version may show ads via **Yandex Mobile Ads**. The ad SDK may collect device and advertising-related data as described below.
- **Optional purchase.** SwiftPaper Pro is a one-time in-app purchase processed by **Google Play Billing**. We do not receive your full payment card details.

---

## 2. Information we process

### 2.1 Files you open or create (on device)

When you scan documents, import images, compress PDFs, or merge PDFs, those files are processed **on your device**. Temporary or output files may be stored in app-accessible storage or shared via Android’s share sheet / Storage Access Framework at your request.

We do **not** upload your documents to our servers for conversion or storage. If you share or save a file using another app or cloud service, that third party’s policy applies.

### 2.2 App preferences (on device)

The App may store local preferences (for example, Pro unlock status, recent job history metadata) using on-device storage such as DataStore. This data remains on your device unless you clear app data or uninstall.

### 2.3 Advertising (Yandex Mobile Ads)

If you use the free (non-Pro) experience, SwiftPaper integrates **Yandex Mobile Ads** to show banner, interstitial, and/or rewarded ads.

The Yandex advertising SDK may collect and process information such as:

- Advertising identifiers (e.g. Google Advertising ID), where available and permitted
- Device and technical data (device model, OS version, app version, language, time zone, IP address / network information)
- Ad interaction data (impressions, clicks, viewability)
- Approximate location derived from IP, if provided by the SDK / network
- Diagnostic information related to ad delivery

This processing is performed by **Yandex** (and its partners) under their own terms and privacy practices. See:

- [Yandex Advertising Network / Mobile Ads privacy information](https://yandex.com/legal/confidential/)
- Yandex Mobile Ads SDK documentation and partner disclosures in Play Console Data safety

You can limit personalized ads via Android settings (e.g. reset/delete advertising ID, opt out of ads personalization where available in your region).

**Pro users:** purchasing SwiftPaper Pro removes ads in the App; ad-related collection by the SDK should not occur while ads are disabled/not loaded. Residual SDK presence in the binary may still be declared in store forms—keep Data safety aligned with actual SDK behavior.

### 2.4 Purchases (Google Play)

In-app purchases are handled by **Google Play Billing**. Google processes payment data under Google’s policies. We may receive purchase tokens / entitlement status to unlock Pro features on your device. We do not store your full credit card number.

### 2.5 Crash and diagnostics (if enabled later)

If we enable Google Play’s pre-launch / vitals, Firebase Crashlytics, or similar tools in a future version, those services may collect crash stacks and device diagnostics. This policy will be updated before such collection begins. **Current shipping intent:** rely on Play Vitals and local processing unless otherwise stated in a later update.

---

## 3. Permissions

Depending on features you use, Android may request:

| Permission / access | Purpose |
|---------------------|---------|
| Camera | Document scanning |
| Photos / media / files (or SAF picker) | Import images and PDFs |
| Internet | Loading ads (Yandex Mobile Ads); Play Billing / license checks as required by Google |
| Notifications | Only if a future version requests them (not required for core PDF tools today) |

We request access only as needed for the feature you invoke.

---

## 4. Children’s privacy

SwiftPaper is not directed at children under 13 (or the equivalent minimum age in your jurisdiction). We do not knowingly collect personal information from children. If you believe a child has provided personal data via ads or the App, contact us and we will take appropriate steps.

---

## 5. Data retention

- **On-device files and history:** retained until you delete them, clear app storage, or uninstall.
- **Ad / analytics data:** retained by Yandex and partners according to their policies.
- **Purchase entitlements:** maintained via Google Play and local Pro flag for as long as you use the App / restore purchases.

---

## 6. Sharing of information

We do not sell your documents. We share information only as follows:

- **Yandex** — advertising SDK data as in §2.3
- **Google** — Play distribution, Billing, and platform services
- **Legal** — if required by law or to protect rights and safety

---

## 7. International transfers

Yandex and Google may process data on servers outside your country. Their safeguards and legal bases are described in their respective privacy policies.

---

## 8. Your choices

- Use **Pro** to remove ads.
- Deny camera/file access (core scan/import features will be limited).
- Reset advertising ID / opt out of personalized ads in system settings.
- Uninstall the App to remove local files and preferences stored in the app sandbox.
- Contact us at the email below for privacy questions.

---

## 9. Changes

We may update this Privacy Policy. The “Effective date” will change when we do. Continued use after an update constitutes acceptance of the revised policy where permitted by law. Material changes will be reflected in the Play Store listing privacy URL when published.

**Public URL (listing):** `https://swiftpaper.app/privacy`  
*(Host this markdown or an HTML version at that URL before release, or update `privacy_policy_url` in the app / Play Console to your live page.)*

---

## 10. Contact

Privacy questions: **privacy@swiftpaper.app** *(placeholder)*  
Developer: SwiftPaper  

If you are in the EEA/UK and need a data controller contact for ad-related processing, also consult Yandex’s disclosures for the Advertising Network.
