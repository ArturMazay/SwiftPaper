package com.swiftpaper.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swiftpaper.app.R
import com.swiftpaper.app.ui.theme.Teal

@Composable
fun SoftSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(18.dp),
        content = content
    )
}

@Composable
fun TealIconWell(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Int = 52
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Teal,
            modifier = Modifier.size((size * 0.54f).dp)
        )
    }
}

/**
 * Artist-crafted custom vector illustration for empty states
 */
@Composable
fun DocumentScanIllustration(
    modifier: Modifier = Modifier,
    sizeDp: Int = 120
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan_beam")
    val beamYRatio by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beam_pos"
    )

    val primaryColor = Teal
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    val cardBg = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier.size(sizeDp.dp)) {
        val w = size.width
        val h = size.height

        // 1. Ambient Background Glow
        drawCircle(
            color = primaryColor.copy(alpha = 0.12f),
            radius = w * 0.42f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        // 2. Back rotated document card
        val backCardWidth = w * 0.45f
        val backCardHeight = h * 0.58f
        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(w * 0.22f, h * 0.18f),
            size = Size(backCardWidth, backCardHeight),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // 3. Main document card
        val cardWidth = w * 0.52f
        val cardHeight = h * 0.65f
        val cardLeft = w * 0.28f
        val cardTop = h * 0.22f

        drawRoundRect(
            color = cardBg,
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
        )
        drawRoundRect(
            color = primaryColor.copy(alpha = 0.4f),
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardWidth, cardHeight),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // Folded Corner
        val foldSize = 16.dp.toPx()
        val foldPath = Path().apply {
            moveTo(cardLeft + cardWidth - foldSize, cardTop)
            lineTo(cardLeft + cardWidth, cardTop + foldSize)
            lineTo(cardLeft + cardWidth - foldSize, cardTop + foldSize)
            close()
        }
        drawPath(foldPath, color = primaryColor.copy(alpha = 0.2f))

        // Document placeholder lines
        val lineX = cardLeft + 12.dp.toPx()
        val lineW = cardWidth - 24.dp.toPx()
        var lineY = cardTop + 20.dp.toPx()

        drawRoundRect(
            color = primaryColor.copy(alpha = 0.6f),
            topLeft = Offset(lineX, lineY),
            size = Size(lineW * 0.6f, 6.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )

        lineY += 14.dp.toPx()
        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(lineX, lineY),
            size = Size(lineW, 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        lineY += 10.dp.toPx()
        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(lineX, lineY),
            size = Size(lineW * 0.85f, 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        lineY += 10.dp.toPx()
        drawRoundRect(
            color = outlineColor,
            topLeft = Offset(lineX, lineY),
            size = Size(lineW * 0.7f, 4.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // 4. Animated Cyan Scanner Beam
        val scanY = cardTop + (cardHeight * beamYRatio)
        drawLine(
            color = primaryColor,
            start = Offset(cardLeft - 8.dp.toPx(), scanY),
            end = Offset(cardLeft + cardWidth + 8.dp.toPx(), scanY),
            strokeWidth = 3.dp.toPx()
        )
        drawLine(
            color = Color.White,
            start = Offset(cardLeft, scanY),
            end = Offset(cardLeft + cardWidth, scanY),
            strokeWidth = 1.5.dp.toPx()
        )
    }
}

@Composable
fun EmptyState(
    icon: ImageVector? = null,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            TealIconWell(icon = icon, size = 64)
        } else {
            DocumentScanIllustration(sizeDp = 130)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    )
}

@Composable
fun LoadingBlock(
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val text = label ?: stringResource(R.string.working)
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(color = Teal)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
