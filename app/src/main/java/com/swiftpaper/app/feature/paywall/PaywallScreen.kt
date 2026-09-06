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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.swiftpaper.app.R
import com.swiftpaper.app.ads.AdsManager
import com.swiftpaper.app.core.UserPrefs
import com.swiftpaper.app.ui.components.SoftSurface
import com.swiftpaper.app.ui.components.TealIconWell
import com.swiftpaper.app.ui.theme.Teal
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    adsManager: AdsManager,
    userPrefs: UserPrefs,
    isProFlow: Flow<Boolean>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val isPro by isProFlow.collectAsState(initial = false)
    var showAdConfirmDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    if (showAdConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showAdConfirmDialog = false },
            title = { Text(stringResource(R.string.pro_ad_dialog_title)) },
            text = { Text(stringResource(R.string.pro_ad_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAdConfirmDialog = false
                        val act = activity
                        if (act == null) {
                            statusMessage = context.getString(R.string.pro_ad_unavailable)
                            return@TextButton
                        }
                        val granted = AtomicBoolean(false)
                        adsManager.showRewarded(
                            activity = act,
                            onRewarded = {
                                granted.set(true)
                                scope.launch {
                                    userPrefs.setPro(true)
                                    statusMessage = context.getString(R.string.pro_unlocked)
                                }
                            },
                            onDismissed = {
                                if (!granted.get()) {
                                    statusMessage =
                                        context.getString(R.string.pro_ad_unavailable)
                                }
                            }
                        )
                    }
                ) {
                    Text(stringResource(R.string.pro_ad_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdConfirmDialog = false }) {
                    Text(stringResource(R.string.pro_ad_dialog_cancel))
                }
            }
        )
    }

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
                    onClick = { showAdConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.get_pro))
                }
            }

            statusMessage?.let {
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
