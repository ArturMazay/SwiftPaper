package com.swiftpaper.app.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.swiftpaper.app.core.UserPrefs
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.YandexAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import java.lang.ref.WeakReference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AdsManager(
    context: Context,
    private val userPrefs: UserPrefs
) : DefaultLifecycleObserver, Application.ActivityLifecycleCallbacks {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var appOpenAd: AppOpenAd? = null
    private var interstitialLoader: InterstitialAdLoader? = null
    private var rewardedLoader: RewardedAdLoader? = null
    private var appOpenLoader: AppOpenAdLoader? = null
    private var sdkReady = false
    private var otherFormatsPreloaded = false
    private var isShowingAppOpen = false
    private var coldStartPending = true
    private var wentToBackgroundAt = 0L
    private var currentActivityRef: WeakReference<Activity>? = null

    fun initialize(onReady: () -> Unit = {}) {
        val app = appContext as Application
        app.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        YandexAds.initialize(appContext) {
            sdkReady = true
            Log.d(TAG, "Yandex Mobile Ads initialized")
            // Prefer App Open first on cold start; other formats after.
            preloadAppOpen()
            onReady()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        val backgroundMs = if (wentToBackgroundAt > 0) {
            System.currentTimeMillis() - wentToBackgroundAt
        } else {
            0L
        }
        // Cold start is handled from MainActivity; here we cover hot start.
        if (!coldStartPending && backgroundMs >= YandexAdsConfig.APP_OPEN_MIN_BACKGROUND_MS) {
            showAppOpenIfAllowed()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        wentToBackgroundAt = System.currentTimeMillis()
        coldStartPending = false
    }

    fun onColdStart(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        // Slight delay so splash/first frame can draw before the ad.
        mainHandler.postDelayed({
            if (coldStartPending) {
                showAppOpenIfAllowed(activity)
                coldStartPending = false
            }
        }, 400L)
    }

    fun preloadAppOpen() {
        if (!sdkReady) return
        if (appOpenLoader == null) {
            appOpenLoader = AppOpenAdLoader(appContext)
        }
        val request = AdRequest.Builder(YandexAdsConfig.APP_OPEN_AD_UNIT_ID).build()
        appOpenLoader?.loadAd(
            request,
            object : AppOpenAdLoadListener {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    Log.d(TAG, "App Open ad loaded")
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Log.w(TAG, "App Open failed: ${error.description}")
                    preloadOtherFormats()
                }
            }
        )
    }

    fun showAppOpenIfAllowed(activity: Activity? = currentActivityRef?.get()) {
        if (activity == null || activity.isFinishing || isShowingAppOpen) {
            preloadOtherFormats()
            return
        }
        val isPro = runBlocking { userPrefs.isPro.first() }
        if (isPro) {
            preloadOtherFormats()
            return
        }
        val lastShown = runBlocking { userPrefs.lastAppOpenAt.first() }
        val now = System.currentTimeMillis()
        if (now - lastShown < YandexAdsConfig.APP_OPEN_COOLDOWN_MS) {
            preloadOtherFormats()
            return
        }
        val ad = appOpenAd
        if (ad == null) {
            preloadAppOpen()
            preloadOtherFormats()
            return
        }
        isShowingAppOpen = true
        ad.setAdEventListener(object : AppOpenAdEventListener {
            override fun onAdShown() {
                runBlocking { userPrefs.setLastAppOpenAt(System.currentTimeMillis()) }
            }

            override fun onAdFailedToShow(adError: AdError) {
                Log.w(TAG, "App Open failed to show: ${adError.description}")
                isShowingAppOpen = false
                clearAppOpen()
                preloadAppOpen()
                preloadOtherFormats()
            }

            override fun onAdDismissed() {
                isShowingAppOpen = false
                clearAppOpen()
                preloadAppOpen()
                preloadOtherFormats()
            }

            override fun onAdClicked() = Unit
            override fun onAdImpression(impressionData: ImpressionData?) = Unit
        })
        ad.show(activity)
    }

    private fun preloadOtherFormats() {
        if (otherFormatsPreloaded) {
            if (interstitialAd == null) preloadInterstitial()
            if (rewardedAd == null) preloadRewarded()
            return
        }
        otherFormatsPreloaded = true
        preloadInterstitial()
        preloadRewarded()
    }

    fun preloadInterstitial() {
        if (!sdkReady) return
        interstitialLoader = InterstitialAdLoader(appContext).also { loader ->
            loader.loadAd(
                AdRequest.Builder(YandexAdsConfig.INTERSTITIAL_AD_UNIT_ID).build(),
                object : InterstitialAdLoadListener {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        this@AdsManager.interstitialAd = interstitialAd
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        Log.w(TAG, "Interstitial failed: ${error.description}")
                    }
                }
            )
        }
    }

    fun preloadRewarded() {
        if (!sdkReady) return
        rewardedLoader = RewardedAdLoader(appContext).also { loader ->
            loader.loadAd(
                AdRequest.Builder(YandexAdsConfig.REWARDED_AD_UNIT_ID).build(),
                object : RewardedAdLoadListener {
                    override fun onAdLoaded(rewarded: RewardedAd) {
                        rewardedAd = rewarded
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        Log.w(TAG, "Rewarded failed: ${error.description}")
                    }
                }
            )
        }
    }

    fun showInterstitialIfAllowed(activity: Activity, onFinished: () -> Unit) {
        val isPro = runBlocking { userPrefs.isPro.first() }
        if (isPro) {
            onFinished()
            return
        }
        val lastShown = runBlocking { userPrefs.lastInterstitialAt.first() }
        val now = System.currentTimeMillis()
        if (now - lastShown < YandexAdsConfig.INTERSTITIAL_COOLDOWN_MS) {
            onFinished()
            return
        }
        val ad = interstitialAd
        if (ad == null) {
            preloadInterstitial()
            onFinished()
            return
        }
        ad.setAdEventListener(object : InterstitialAdEventListener {
            override fun onAdShown() {
                runBlocking { userPrefs.setLastInterstitialAt(System.currentTimeMillis()) }
            }

            override fun onAdFailedToShow(adError: AdError) {
                clearInterstitial()
                preloadInterstitial()
                onFinished()
            }

            override fun onAdDismissed() {
                clearInterstitial()
                preloadInterstitial()
                onFinished()
            }

            override fun onAdClicked() = Unit
            override fun onAdImpression(impressionData: ImpressionData?) = Unit
        })
        ad.show(activity)
    }

    fun showRewarded(activity: Activity, onRewarded: () -> Unit, onDismissed: () -> Unit = {}) {
        val isPro = runBlocking { userPrefs.isPro.first() }
        if (isPro) {
            onRewarded()
            return
        }
        val ad = rewardedAd
        if (ad == null) {
            preloadRewarded()
            onDismissed()
            return
        }
        ad.setAdEventListener(object : RewardedAdEventListener {
            override fun onAdShown() = Unit
            override fun onAdFailedToShow(adError: AdError) {
                clearRewarded()
                preloadRewarded()
                onDismissed()
            }

            override fun onAdDismissed() {
                clearRewarded()
                preloadRewarded()
                onDismissed()
            }

            override fun onAdClicked() = Unit
            override fun onAdImpression(impressionData: ImpressionData?) = Unit
            override fun onRewarded(reward: Reward) {
                onRewarded()
            }
        })
        ad.show(activity)
    }

    private fun clearAppOpen() {
        appOpenAd?.setAdEventListener(null)
        appOpenAd = null
    }

    private fun clearInterstitial() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

    private fun clearRewarded() {
        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }
    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }
    override fun onActivityPaused(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityRef?.get() === activity) {
            currentActivityRef = null
        }
    }

    companion object {
        private const val TAG = "SwiftPaperAds"
    }
}
