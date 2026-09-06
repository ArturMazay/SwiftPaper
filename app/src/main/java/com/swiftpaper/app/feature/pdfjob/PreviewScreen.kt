package com.swiftpaper.app.feature.pdfjob

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.swiftpaper.app.R
import com.swiftpaper.app.SwiftPaperApp
import com.swiftpaper.app.navigation.JobType
import com.swiftpaper.app.ui.components.EmptyState
import com.swiftpaper.app.ui.components.ErrorBanner
import com.swiftpaper.app.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    title: String,
    jobType: JobType,
    viewModel: PdfJobViewModel,
    onBack: () -> Unit,
    onExported: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val app = context.applicationContext as SwiftPaperApp
    val activity = context as? Activity
    val isPro by app.userPrefs.isPro.collectAsState(initial = false)

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) viewModel.addUris(uris)
    }

    LaunchedEffect(jobType) {
        if (jobType == JobType.IMAGE_TO_PDF && state.uris.isEmpty()) {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            if (state.uris.isEmpty()) {
                                stringResource(R.string.add_pages_to_continue)
                            } else {
                                pluralStringResource(
                                    R.plurals.pages_count,
                                    state.uris.size,
                                    state.uris.size
                                )
                            },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        picker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (state.uris.isEmpty()) {
                            stringResource(R.string.add_images)
                        } else {
                            stringResource(R.string.add_more)
                        }
                    )
                }
                Button(
                    onClick = { viewModel.export(jobType, onExported) },
                    enabled = state.uris.isNotEmpty() && !state.isWorking,
                    modifier = Modifier.weight(1f)
                ) {
                    if (state.isWorking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.export_pdf))
                    }
                }
            }

            if (!isPro && !(state.useHd && state.skipWatermark)) {
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
            }
            if (state.useHd || isPro) {
                Text(
                    stringResource(R.string.hd_no_watermark_unlocked),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            state.error?.let { ErrorBanner(it) }

            if (state.uris.isEmpty() && !state.isWorking) {
                EmptyState(
                    icon = Icons.Default.Add,
                    title = stringResource(R.string.no_pages_yet),
                    message = stringResource(R.string.no_pages_message),
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 28.dp, top = 4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(state.uris) { index, uri ->
                        PageRow(
                            index = index,
                            uri = uri,
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
private fun PageRow(
    index: Int,
    uri: Uri,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                stringResource(R.string.page_number, index + 1),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(R.string.drag_order_with_arrows),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
        IconButton(onClick = onMoveUp, enabled = canMoveUp) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.cd_move_up),
                tint = Teal
            )
        }
        IconButton(onClick = onMoveDown, enabled = canMoveDown) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.cd_move_down),
                tint = Teal
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
