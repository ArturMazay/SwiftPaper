package com.swiftpaper.app.feature.export

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swiftpaper.app.R
import com.swiftpaper.app.ads.AdsManager
import com.swiftpaper.app.ads.YandexBannerAd
import com.swiftpaper.app.core.FileExporter
import com.swiftpaper.app.core.HistoryRepository
import com.swiftpaper.app.core.UserPrefs
import com.swiftpaper.app.ui.components.SoftSurface
import com.swiftpaper.app.ui.components.TealIconWell
import com.swiftpaper.app.ui.theme.Teal
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    filePath: String,
    mimeType: String,
    adsManager: AdsManager,
    @Suppress("UNUSED_PARAMETER") historyRepository: HistoryRepository,
    userPrefs: UserPrefs,
    onHome: () -> Unit,
    onPaywall: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val isPro by userPrefs.isPro.collectAsState(initial = false)
    val sessionHd by userPrefs.sessionHdUnlock.collectAsState(initial = false)
    var statusMessage by remember { mutableStateOf<String?>(null) }
    val file = remember(filePath) { File(filePath) }
    val exportDefaultName = stringResource(R.string.export_default_name)
    val fileName = file.name.ifBlank { exportDefaultName }
    val fileNotFound = stringResource(R.string.file_not_found)
    val hdUnlockedAgain = stringResource(R.string.hd_unlocked_export_again)

    LaunchedEffect(filePath) {
        activity?.let { adsManager.showInterstitialIfAllowed(it) {} }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.export)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = { YandexBannerAd() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            TealIconWell(icon = Icons.Default.Check, size = 72)
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.ready_to_share),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                fileName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            SoftSurface {
                Text(
                    stringResource(R.string.export_saved_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }

            statusMessage?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Teal,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = {
                    if (file.exists()) {
                        FileExporter.shareFile(context, file, mimeType)
                    } else {
                        statusMessage = fileNotFound
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.share))
            }

            if (!isPro && !sessionHd) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        val act = activity ?: return@OutlinedButton
                        adsManager.showRewarded(
                            activity = act,
                            onRewarded = {
                                scope.launch {
                                    userPrefs.setSessionHdUnlock(true)
                                    statusMessage = hdUnlockedAgain
                                }
                            },
                            onDismissed = {}
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.watch_ad_hd))
                }
            } else if (!isPro && sessionHd) {
                Spacer(Modifier.height(10.dp))
                Text(
                    stringResource(R.string.hd_unlock_active),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Teal,
                    textAlign = TextAlign.Center
                )
            }

            if (!isPro) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onPaywall,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.go_pro))
                }
            }

            Spacer(Modifier.weight(1f))
            TextButton(onClick = onHome) {
                Text(stringResource(R.string.back_to_home))
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
