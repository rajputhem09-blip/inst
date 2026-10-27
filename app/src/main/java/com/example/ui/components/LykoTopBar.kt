package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LykoGradientBrush
import com.example.ui.theme.LykoLiveGradient
import com.example.ui.theme.LykoMagentaSecondary
import com.example.ui.theme.LykoVioletPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LykoTopBar(
    unreadNotificationsCount: Int = 2,
    unreadMessagesCount: Int = 1,
    onNotificationsClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onLiveClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    isAdmin: Boolean = true
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LYKO",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = LykoVioletPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(LykoMagentaSecondary)
                )
            }
        },
        actions = {
            // Live Stream Browse shortcut
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(LykoLiveGradient)
                    .clickable { onLiveClick() }
                    .testTag("topbar_live_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Admin Panel button (if admin)
            if (isAdmin) {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier.testTag("topbar_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AdminPanelSettings,
                        contentDescription = "Admin Panel",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Notifications Bell
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable { onNotificationsClick() }
            ) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.testTag("topbar_notifications_button")
                ) {
                    Icon(
                        imageVector = if (unreadNotificationsCount > 0) Icons.Default.Notifications else Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (unreadNotificationsCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LykoMagentaSecondary)
                    )
                }
            }

            // Direct Messages
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clickable { onMessagesClick() }
            ) {
                IconButton(
                    onClick = onMessagesClick,
                    modifier = Modifier.testTag("topbar_messages_button")
                ) {
                    Icon(
                        imageVector = if (unreadMessagesCount > 0) Icons.Default.Send else Icons.Outlined.Send,
                        contentDescription = "Direct Messages",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (unreadMessagesCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LykoVioletPrimary)
                    )
                }
            }
        }
    )
}
