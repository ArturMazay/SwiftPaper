package com.swiftpaper.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.swiftpaper.app.SwiftPaperApp
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

/**
 * Banner via AndroidView + BannerAdView.
 * Prefer this over mobileads-compose when the Compose Banner API differs across SDK versions.
 */
@Composable
fun YandexBannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = context.applicationContext as SwiftPaperApp
    val isPro by app.userPrefs.isPro.collectAsState(initial = false)
    if (isPro) return

    val widthDp = LocalConfiguration.current.screenWidthDp.coerceAtLeast(320)

    val bannerView = remember(widthDp) {
        BannerAdView(context).apply {
            setAdSize(BannerAdSize.sticky(context, widthDp))
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() = Unit
                override fun onAdFailedToLoad(error: AdRequestError) = Unit
                override fun onAdClicked() = Unit
                override fun onImpression(impressionData: ImpressionData?) = Unit
            })
            // Yandex Ads SDK 8: ad unit id lives on AdRequest.
            loadAd(AdRequest.Builder(YandexAdsConfig.BANNER_AD_UNIT_ID).build())
        }
    }

    DisposableEffect(bannerView) {
        onDispose { bannerView.destroy() }
    }

    AndroidView(
        factory = { bannerView },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}
