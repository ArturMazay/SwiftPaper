package com.swiftpaper.app.ads

import com.swiftpaper.app.BuildConfig

object YandexAdsConfig {
    const val BANNER_AD_UNIT_ID = BuildConfig.YANDEX_BANNER_AD_UNIT_ID
    const val INTERSTITIAL_AD_UNIT_ID = BuildConfig.YANDEX_INTERSTITIAL_AD_UNIT_ID
    const val REWARDED_AD_UNIT_ID = BuildConfig.YANDEX_REWARDED_AD_UNIT_ID
    const val APP_OPEN_AD_UNIT_ID = BuildConfig.YANDEX_APP_OPEN_AD_UNIT_ID

    /** Minimum gap between interstitial impressions. */
    const val INTERSTITIAL_COOLDOWN_MS = 100_000L

    /** Don't show app-open more often than this (cold + hot starts). */
    const val APP_OPEN_COOLDOWN_MS = 4 * 60 * 1000L

    /** Hot start: app must have been in background at least this long. */
    const val APP_OPEN_MIN_BACKGROUND_MS = 30_000L
}
