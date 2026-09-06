# Soft launch plan — SwiftPaper

Closed → open testing, metrics, ads/billing go-live, RU payout notes, and iteration with Cursor.

**Package:** `com.swiftpaper.app`  
**Version baseline:** `1.0.0` (versionCode `1`)  
**Pro product ID:** `swiftpaper_pro` (see Play Billing below)

---

## 1. Testing tracks

### Phase A — Internal testing (1–3 days)

- Upload signed AAB to **Internal testing**.
- Testers: you + 2–5 devices (low/mid Android 8–14).
- Checklist: scan → PDF, image → PDF, compress, merge, export/share, History, Pro purchase (license testers), ads with **real** unit IDs on a release build.
- Fix crashers before closed testing.

### Phase B — Closed testing (soft launch)

**Suggested countries (start small, RU-friendly + English listing):**

| Wave | Countries | Goal |
|------|-----------|------|
| B1 | **Russia**, **Kazakhstan**, **Belarus** (or CIS subset available in Console) | Yandex Ads fill rates, RU UX, payout path validation |
| B2 | **India**, **Indonesia**, **Brazil**, **Turkey**, **Mexico** | Volume + diverse devices; English listing |
| B3 (optional) | **Poland**, **Germany**, **UK**, **US** | Quality bar before production |

**Recruitment**
- Closed track link + email list / Telegram / friends.
- Target **50–200** testers before open testing; enough for crash + D1 signal.

**Duration:** 7–14 days on B1, then expand or fix.

### Phase C — Open testing

- Same AAB (or next version) on **Open testing**, countries = B1+B2 (or all except production holdouts).
- Public Play link; still pre-production.
- Watch reviews and vitals 3–7 days, then promote to **Production** when gates pass (§2).

### Phase D — Production

- Staged rollout: **10% → 50% → 100%** over several days.
- Pause if crash-free or ANR regresses.

---

## 2. Success metrics (gates)

Instrument manually via Play Console + simple event notes until analytics ships.

| Metric | Soft-launch target | Action if miss |
|--------|--------------------|----------------|
| **Crash-free users** (Play Vitals, 7d) | ≥ **99.5%** (never below 99%) | Hotfix before expanding countries |
| **ANR rate** | Within Play “bad behavior” thresholds / peer baseline | Profile main thread (PDF I/O off UI) |
| **D1 retention** | ≥ **25–35%** (utility apps vary; track trend) | Improve first-run: home clarity, first successful export |
| **D7 retention** | ≥ **10–15%** | Push value: History, Pro, faster compress |
| **Export / session** | ≥ **0.6** successful exports per active session | Fix export/share UX; reduce friction before paywall/ads |
| **Ad fill / show errors** | Banner + interstitial error rate trending down; rewarded completes for HD unlock | Verify unit IDs, app-ads, network (see `YANDEX_ADS_SETUP.md`) |
| **Pro conversion** (testers / open) | Track purchase attempts → success; no `ITEM_UNAVAILABLE` | Confirm `swiftpaper_pro` active, base plan, license testers |

Log weekly in a short note (Notion/issue): crash-free, D1/D7, export/session, ad revenue (Yandex), Pro revenue (Play).

---

## 3. Yandex Ads — real unit IDs checklist

Before any external testers see ads:

- [ ] Yandex Advertising Network app created for `com.swiftpaper.app`
- [ ] Units created: **Banner**, **Interstitial**, **Rewarded**
- [ ] Demo IDs replaced in `app/build.gradle.kts` BuildConfig (see `store/YANDEX_ADS_SETUP.md`)
- [ ] Release/internal AAB built with real IDs (never ship `demo-*-yandex` to Play)
- [ ] Test on device: banner on Home, interstitial cooldown path, rewarded “HD / no watermark”
- [ ] Pro: ads suppressed when `isPro == true`
- [ ] Data safety + privacy policy mention Yandex
- [ ] Note block/app ID and unit IDs in a private password manager (not in git if sensitive)

---

## 4. Play Billing — `swiftpaper_pro`

In Play Console → Monetize → Products → In-app products:

| Field | Value |
|-------|--------|
| Product ID | `swiftpaper_pro` *(must match `BuildConfig.PRO_PRODUCT_ID`)* |
| Type | **One-time** (managed product / in-app product — not subscription) |
| Name | SwiftPaper Pro |
| Description | Remove ads, HD exports without watermark, batch & unlimited merges. One-time unlock. |
| Status | **Active** |
| Price | Set per soft-launch countries (start with a mid tier in RUB + USD equivalents) |

Checklist:

- [ ] Product ID exactly `swiftpaper_pro`
- [ ] License testers added (Settings → License testing)
- [ ] Internal/closed build can complete purchase + acknowledge (see `BillingManager`)
- [ ] Restore / reinstall keeps Pro via `queryPurchases`
- [ ] Pro UI: no banner, no interstitial/rewarded pressure

---

## 5. Payout account note (RU developers)

**Two money pipes — configure both early:**

| Source | Pays via | Typical RU developer note |
|--------|----------|---------------------------|
| **Google Play** (Pro IAP) | Google payments profile / merchant account | Availability and payout methods depend on developer account country and Google’s current policies. Confirm your Play Console payments profile can receive funds for your legal entity/individual in RU (or use an allowed entity/country setup your counsel advises). Incomplete payments profile = **no IAP**. |
| **Yandex Ads** | Yandex Advertising Network payout | Often the smoother path for **ad revenue** targeting RU/CIS traffic. Register as publisher, complete tax/payout details in Yandex cabinet, verify minimum payout and currency. Ad revenue does **not** flow through Google Play. |

Actions before soft launch:

1. Complete **Play Console** payments / tax / business identity so `swiftpaper_pro` can sell.
2. Complete **Yandex** publisher payout profile so demo → real ads can eventually pay out.
3. Do not assume Yandex ad revenue appears in Play earnings — check both dashboards weekly.
4. Keep legal entity / self-employed (самозанятый / ИП) docs consistent across cabinets.

---

## 6. Iteration loop with Cursor

Cadence during closed/open testing:

1. **Pull signals** — Play Vitals, tester feedback, Yandex stats (fill, CTR), Billing errors.
2. **File an issue** in one sentence: symptom, device, Android version, steps.
3. **In Cursor** — open the repo, paste vitals/stack or UI bug, ask for a minimal fix (keep scope tight: one bug or one ASO tweak).
4. **Bump** `versionCode` / `versionName`, build AAB, upload to the **same** testing track.
5. **Retest** the failing path + smoke Scan / Compress / Merge / Export / Pro.
6. **Update** `store/` docs only if privacy, ads, or product IDs change.
7. **Gate check** — if crash-free and export/session meet targets for 3+ days, expand countries or promote track.

Prompt template for Cursor:

```
SwiftPaper soft launch issue:
Device / Android:
Track (internal/closed/open):
Steps:
Expected vs actual:
Logs / vitals:
Please fix with minimal diff; do not expand scope.
```

---

## 7. Soft-launch exit criteria → Production

- [ ] Crash-free ≥ 99.5% on recent build  
- [ ] No P0 bugs on scan / export / purchase  
- [ ] Real Yandex units live; no demo IDs in release  
- [ ] `swiftpaper_pro` purchasable in all soft-launch countries  
- [ ] Privacy policy URL live; Data safety submitted  
- [ ] Listing screenshots + feature graphic uploaded (see `PLAY_LISTING_EN.md`)  
- [ ] Payments profiles OK for Play + Yandex  

Then: staged production rollout.
