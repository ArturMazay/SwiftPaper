package com.swiftpaper.app.feature.paywall

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.swiftpaper.app.R
import com.swiftpaper.app.billing.BillingManager
import com.swiftpaper.app.ui.components.SoftSurface
import com.swiftpaper.app.ui.components.TealIconWell
import com.swiftpaper.app.ui.theme.Teal
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    billingManager: BillingManager,
    isProFlow: Flow<Boolean>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isPro by isProFlow.collectAsState(initial = false)
    val product by billingManager.productDetails.collectAsState()
    val status by billingManager.statusMessage.collectAsState()
    val priceLabel = product
        ?.oneTimePurchaseOfferDetailsList
        ?.firstOrNull()
        ?.formattedPrice
        ?: stringResource(R.string.price_one_time)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pro_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))
            TealIconWell(icon = Icons.Default.Star, size = 72)
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.pro_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.pro_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(28.dp))

            SoftSurface {
                BenefitRow(
                    icon = Icons.Default.Clear,
                    title = stringResource(R.string.pro_benefit_ads)
                )
                Spacer(Modifier.height(16.dp))
                BenefitRow(
                    icon = Icons.Default.Star,
                    title = stringResource(R.string.pro_benefit_hd)
                )
                Spacer(Modifier.height(16.dp))
                BenefitRow(
                    icon = Icons.Default.List,
                    title = stringResource(R.string.pro_benefit_batch)
                )
            }

            Spacer(Modifier.height(28.dp))

            if (isPro) {
                Text(
                    stringResource(R.string.already_pro),
                    style = MaterialTheme.typography.titleLarge,
                    color = Teal
                )
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onBack) {
                    Text(stringResource(R.string.continue_action))
                }
            } else {
                Button(
                    onClick = { activity?.let { billingManager.launchPurchase(it) } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(
                            R.string.buy_pro_with_price,
                            stringResource(R.string.buy_pro),
                            priceLabel
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { billingManager.restorePurchases() }) {
                    Text(stringResource(R.string.restore_purchase))
                }
            }

            status?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BenefitRow(
    icon: ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, tint = Teal, modifier = Modifier.size(24.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = Teal,
            modifier = Modifier.size(20.dp)
        )
    }
}
