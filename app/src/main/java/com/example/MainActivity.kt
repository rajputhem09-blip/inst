package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.LykoBottomNavBar
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.create.CreateHubScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.feed.FeedScreen
import com.example.ui.screens.feed.StoryViewerScreen
import com.example.ui.screens.live.LiveStreamBrowseScreen
import com.example.ui.screens.live.LiveStreamRoomScreen
import com.example.ui.screens.messages.ChatDetailScreen
import com.example.ui.screens.messages.MessagesScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.UserProfileDetailScreen
import com.example.ui.screens.reels.ReelsScreen
import com.example.ui.screens.settings.SettingsPrivacyScreen
import com.example.ui.theme.LykoDarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                LykoMainApp()
            }
        }
    }
}

@Composable
fun LykoMainApp(
    viewModel: LykoViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Handle Android system back button
    BackHandler(enabled = currentScreen != NavigationDestination.FEED) {
        when (currentScreen) {
            NavigationDestination.CHAT_DETAIL -> viewModel.navigateTo(NavigationDestination.MESSAGES)
            NavigationDestination.LIVE_ROOM -> viewModel.navigateTo(NavigationDestination.LIVE_BROWSE)
            NavigationDestination.USER_PROFILE_DETAIL -> viewModel.navigateTo(NavigationDestination.EXPLORE)
            NavigationDestination.SETTINGS_PRIVACY -> viewModel.navigateTo(NavigationDestination.PROFILE)
            else -> viewModel.navigateTo(NavigationDestination.FEED)
        }
    }

    val showBottomBar = currentScreen in listOf(
        NavigationDestination.FEED,
        NavigationDestination.EXPLORE,
        NavigationDestination.CREATE,
        NavigationDestination.REELS,
        NavigationDestination.PROFILE
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LykoDarkBackground),
        bottomBar = {
            if (showBottomBar) {
                LykoBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                )
            }
        },
        snackbarHost = {
            userMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shadowElevation = 8.dp,
                        modifier = Modifier.testTag("user_toast_message")
                    ) {
                        Text(
                            text = msg,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (showBottomBar) PaddingValues(bottom = innerPadding.calculateBottomPadding()) else PaddingValues(0.dp))
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { destination ->
                when (destination) {
                    NavigationDestination.FEED -> FeedScreen(viewModel = viewModel)
                    NavigationDestination.EXPLORE -> ExploreScreen(viewModel = viewModel)
                    NavigationDestination.CREATE, NavigationDestination.GO_LIVE -> CreateHubScreen(viewModel = viewModel)
                    NavigationDestination.REELS -> ReelsScreen(viewModel = viewModel)
                    NavigationDestination.PROFILE -> ProfileScreen(viewModel = viewModel)
                    NavigationDestination.MESSAGES -> MessagesScreen(viewModel = viewModel)
                    NavigationDestination.CHAT_DETAIL -> ChatDetailScreen(viewModel = viewModel)
                    NavigationDestination.STORY_VIEWER -> StoryViewerScreen(viewModel = viewModel)
                    NavigationDestination.LIVE_BROWSE -> LiveStreamBrowseScreen(viewModel = viewModel)
                    NavigationDestination.LIVE_ROOM -> LiveStreamRoomScreen(viewModel = viewModel)
                    NavigationDestination.ADMIN_PANEL -> AdminPanelScreen(viewModel = viewModel)
                    NavigationDestination.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                    NavigationDestination.SETTINGS_PRIVACY -> SettingsPrivacyScreen(viewModel = viewModel)
                    NavigationDestination.USER_PROFILE_DETAIL -> UserProfileDetailScreen(viewModel = viewModel)
                }
            }
        }
    }
}
