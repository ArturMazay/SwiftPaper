package com.swiftpaper.app

import android.app.Application
import com.swiftpaper.app.ads.AdsManager
import com.swiftpaper.app.billing.BillingManager
import com.swiftpaper.app.core.HistoryRepository
import com.swiftpaper.app.core.UserPrefs

class SwiftPaperApp : Application() {
    lateinit var userPrefs: UserPrefs
        private set
    lateinit var historyRepository: HistoryRepository
        private set
    lateinit var adsManager: AdsManager
        private set
    lateinit var billingManager: BillingManager
        private set

    override fun onCreate() {
        super.onCreate()
        userPrefs = UserPrefs(this)
        historyRepository = HistoryRepository(this)
        adsManager = AdsManager(this, userPrefs)
        billingManager = BillingManager(this, userPrefs)
        adsManager.initialize()
        billingManager.start()
    }
}
