package com.swiftpaper.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.swiftpaper.app.R
import com.swiftpaper.app.ads.YandexBannerAd
import com.swiftpaper.app.ui.components.TealIconWell
import com.swiftpaper.app.ui.theme.Teal
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isProFlow: Flow<Boolean>,
    onScan: () -> Unit,
    onImageToPdf: () -> Unit,
    onCompress: () -> Unit,
    onMerge: () -> Unit,
    onHistory: () -> Unit,
    onPaywall: () -> Unit
) {
    val isPro by isProFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            stringResource(R.string.tagline),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHistory) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = stringResource(R.string.cd_history)
                        )
                    }
                    IconButton(onClick = onPaywall) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = stringResource(R.string.cd_pro),
                            tint = if (isPro) Teal else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            ActionTile(
                title = stringResource(R.string.action_scan),
                subtitle = stringResource(R.string.home_scan_subtitle),
                icon = Icons.Default.Add,
                onClick = onScan
            )
            ActionTile(
                title = stringResource(R.string.action_image_to_pdf),
                subtitle = stringResource(R.string.home_image_to_pdf_subtitle),
                icon = Icons.Default.Share,
                onClick = onImageToPdf
            )
            ActionTile(
                title = stringResource(R.string.action_compress),
                subtitle = stringResource(R.string.home_compress_subtitle),
                icon = Icons.Default.Add,
                onClick = onCompress
            )
            ActionTile(
                title = stringResource(R.string.action_merge),
                subtitle = stringResource(R.string.home_merge_subtitle),
                icon = Icons.Default.Add,
                onClick = onMerge
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TealIconWell(icon = icon)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}
