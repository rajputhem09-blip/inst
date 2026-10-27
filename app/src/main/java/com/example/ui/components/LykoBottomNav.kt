package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LykoGradientBrush
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.NavigationDestination

@Composable
fun LykoBottomNavBar(
    currentScreen: NavigationDestination,
    onNavigate: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("lyko_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // Feed / Home
        NavigationBarItem(
            selected = currentScreen == NavigationDestination.FEED,
            onClick = { onNavigate(NavigationDestination.FEED) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NavigationDestination.FEED) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home Feed"
                )
            },
            label = { Text("Feed") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = LykoVioletPrimary,
                indicatorColor = LykoVioletPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_feed")
        )

        // Explore
        NavigationBarItem(
            selected = currentScreen == NavigationDestination.EXPLORE,
            onClick = { onNavigate(NavigationDestination.EXPLORE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NavigationDestination.EXPLORE) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Explore"
                )
            },
            label = { Text("Explore") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = LykoVioletPrimary,
                indicatorColor = LykoVioletPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_explore")
        )

        // Create Hub (+)
        NavigationBarItem(
            selected = currentScreen == NavigationDestination.CREATE,
            onClick = { onNavigate(NavigationDestination.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LykoGradientBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            label = { Text("Create") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = LykoVioletPrimary,
                indicatorColor = Color.Transparent,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_create")
        )

        // Reels / Clips
        NavigationBarItem(
            selected = currentScreen == NavigationDestination.REELS,
            onClick = { onNavigate(NavigationDestination.REELS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NavigationDestination.REELS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircle,
                    contentDescription = "Reels"
                )
            },
            label = { Text("Reels") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = LykoVioletPrimary,
                indicatorColor = LykoVioletPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_reels")
        )

        // Profile
        NavigationBarItem(
            selected = currentScreen == NavigationDestination.PROFILE,
            onClick = { onNavigate(NavigationDestination.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == NavigationDestination.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = LykoVioletPrimary,
                indicatorColor = LykoVioletPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_item_profile")
        )
    }
}
