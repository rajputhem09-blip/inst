package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.VerificationStatus
import com.example.ui.theme.LykoCyanTertiary
import com.example.ui.theme.LykoVioletPrimary

@Composable
fun VerifiedBadge(
    status: VerificationStatus,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    if (status == VerificationStatus.NONE) return

    val gradient = when (status) {
        VerificationStatus.VERIFIED_ADMIN -> Brush.linearGradient(
            listOf(Color(0xFFEF4444), Color(0xFFF59E0B))
        )
        VerificationStatus.VERIFIED_OFFICIAL -> Brush.linearGradient(
            listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))
        )
        VerificationStatus.VERIFIED_CREATOR -> Brush.linearGradient(
            listOf(LykoVioletPrimary, Color(0xFFEC4899))
        )
        VerificationStatus.NONE -> Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (status == VerificationStatus.VERIFIED_ADMIN) Icons.Default.Shield else Icons.Default.Check,
            contentDescription = "Verified Account",
            tint = Color.White,
            modifier = Modifier.size(size * 0.65f)
        )
    }
}
