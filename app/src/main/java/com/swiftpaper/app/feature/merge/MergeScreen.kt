package com.swiftpaper.app.feature.merge

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swiftpaper.app.R
import com.swiftpaper.app.SwiftPaperApp
import com.swiftpaper.app.feature.pdfjob.PdfJobViewModel
import com.swiftpaper.app.navigation.JobType
import com.swiftpaper.app.ui.components.EmptyState
import com.swiftpaper.app.ui.components.ErrorBanner
import com.swiftpaper.app.ui.components.TealIconWell
import com.swiftpaper.app.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergeScreen(
    viewModel: PdfJobViewModel,
    onBack: () -> Unit,
    onExported: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val app = context.applicationContext as SwiftPaperApp
    val activity = context as? Activity
    val isPro by app.userPrefs.isPro.collectAsState(initial = false)
    val sessionHd by app.userPrefs.sessionHdUnlock.collectAsState(initial = false)
    val hdReady = isPro || state.useHd || sessionHd

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            viewModel.addUris(uris)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.action_merge),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            stringResource(R.string.merge_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clear()
                        onBack()
                    }) {
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { picker.launch(arrayOf("application/pdf")) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (state.uris.isEmpty()) {
                            stringResource(R.string.add_pdfs)
                        } else {
                            stringResource(R.string.add_more)
                        }
                    )
                }
                Button(
                    onClick = { viewModel.export(JobType.MERGE, onExported) },
                    enabled = state.uris.size >= 2 && !state.isWorking,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isWorking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.merge_action))
                    }
                }
            }

            if (!hdReady) {
                OutlinedButton(
                    onClick = {
                        activity?.let { act ->
                            app.adsManager.showRewarded(
                                activity = act,
                                onRewarded = { viewModel.unlockHdNoWatermark() }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.watch_ad_hd))
                }
            } else if (!isPro) {
                Text(
                    stringResource(R.string.hd_no_watermark_unlocked),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Teal
                )
            }

            state.error?.let { ErrorBanner(it) }

            if (state.uris.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Share,
                    title = stringResource(R.string.no_pdfs_yet),
                    message = stringResource(R.string.no_pdfs_message),
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    pluralStringResource(
                        R.plurals.files_selected,
                        state.uris.size,
                        state.uris.size
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 28.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(state.uris) { index, uri ->
                        MergeFileRow(
                            index = index,
                            label = uri.lastPathSegment
                                ?: stringResource(R.string.pdf_fallback_label, index + 1),
                            canMoveUp = index > 0,
                            canMoveDown = index < state.uris.lastIndex,
                            onMoveUp = { viewModel.move(index, index - 1) },
                            onMoveDown = { viewModel.move(index, index + 1) },
                            onRemove = { viewModel.removeAt(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MergeFileRow(
    index: Int,
    label: String,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TealIconWell(icon = Icons.Default.Share, size = 44)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(R.string.file_number, index + 1),
                style = MaterialTheme.typography.labelLarge,
                color = Teal
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onMoveUp, enabled = canMoveUp) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.cd_move_up)
            )
        }
        IconButton(onClick = onMoveDown, enabled = canMoveDown) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.cd_move_down)
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.cd_remove)
            )
        }
    }
}
