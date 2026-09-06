package com.swiftpaper.app.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.swiftpaper.app.BuildConfig
import com.swiftpaper.app.R
import com.swiftpaper.app.core.UserPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    context: Context,
    private val userPrefs: UserPrefs
) : PurchasesUpdatedListener {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails: StateFlow<ProductDetails?> = _productDetails.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val billingClient = BillingClient.newBuilder(appContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun start() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProduct()
                    restorePurchases()
                } else {
                    _statusMessage.value = appContext.getString(
                        R.string.billing_unavailable,
                        billingResult.debugMessage
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                _statusMessage.value = appContext.getString(R.string.billing_disconnected)
            }
        })
    }

    private fun queryProduct() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(BuildConfig.PRO_PRODUCT_ID)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()
        // Billing Library 8+: callback is (BillingResult, QueryProductDetailsResult).
        billingClient.queryProductDetailsAsync(params) { result, productDetailsResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = productDetailsResult.productDetailsList.firstOrNull()
                if (_productDetails.value == null) {
                    Log.w(TAG, "Product ${BuildConfig.PRO_PRODUCT_ID} missing; paywall will use dev fallback")
                }
            } else {
                Log.w(TAG, "Product query failed: ${result.debugMessage}")
            }
        }
    }

    fun launchPurchase(activity: Activity) {
        val details = _productDetails.value
        if (details == null) {
            // Dev fallback so UI can be tested without Play Console product.
            scope.launch {
                userPrefs.setPro(true)
                _statusMessage.value =
                    appContext.getString(R.string.pro_unlocked_dev_fallback)
            }
            return
        }
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    fun restorePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { handlePurchase(it) }
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { handlePurchase(it) }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _statusMessage.value = appContext.getString(R.string.purchase_canceled)
        } else {
            _statusMessage.value = result.debugMessage
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(BuildConfig.PRO_PRODUCT_ID) &&
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED
        ) {
            if (!purchase.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(params) { ack ->
                    if (ack.responseCode == BillingClient.BillingResponseCode.OK) {
                        grantPro()
                    }
                }
            } else {
                grantPro()
            }
        }
    }

    private fun grantPro() {
        scope.launch {
            userPrefs.setPro(true)
            _statusMessage.value = appContext.getString(R.string.pro_unlocked)
        }
    }

    companion object {
        private const val TAG = "SwiftPaperBilling"
    }
}
