package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LykoVioletPrimary

@Composable
fun AudioWaveformPlayer(
    durationSeconds: Int = 18,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            val totalSteps = durationSeconds * 10
            while (isPlaying && currentProgress < 1f) {
                kotlinx.coroutines.delay(100)
                currentProgress += 1f / totalSteps
            }
            if (currentProgress >= 1f) {
                isPlaying = false
                currentProgress = 0f
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(LykoVioletPrimary)
                .clickable {
                    isPlaying = !isPlaying
                    if (currentProgress >= 1f) currentProgress = 0f
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Stop voice note" else "Play voice note",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Waveform Bars
        val barHeights = remember {
            listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.7f, 1.0f, 0.5f, 0.8f, 0.4f, 0.6f, 0.9f, 0.3f, 0.7f, 0.5f, 0.8f, 0.4f, 0.7f, 0.3f)
        }
        val activeColor = LykoVioletPrimary
        val inactiveColor = MaterialTheme.colorScheme.outlineVariant

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
        ) {
            val barCount = barHeights.size
            val barWidth = 3.dp.toPx()
            val totalBarWidth = barCount * barWidth
            val spacing = (size.width - totalBarWidth) / (barCount - 1).coerceAtLeast(1)

            barHeights.forEachIndexed { index, heightRatio ->
                val progressThreshold = index.toFloat() / barCount
                val color = if (currentProgress >= progressThreshold) activeColor else inactiveColor
                val barHeight = size.height * heightRatio
                val top = (size.height - barHeight) / 2f
                val left = index * (barWidth + spacing)

                drawRoundRect(
                    color = color,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        val elapsed = (durationSeconds * currentProgress).toInt()
        val displaySeconds = if (isPlaying) elapsed else durationSeconds
        val formattedTime = "0:${if (displaySeconds < 10) "0$displaySeconds" else "$displaySeconds"}"

        Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
