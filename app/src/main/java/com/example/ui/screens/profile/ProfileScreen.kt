package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.PostType
import com.example.data.model.UserRole
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LykoMagentaSecondary
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

enum class ProfileTab {
    POSTS,
    REELS,
    SAVED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val feedPosts by viewModel.feedPosts.collectAsState()

    var activeTab by remember { mutableStateOf(ProfileTab.POSTS) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    // User's own posts and saved posts
    val myPosts = remember(feedPosts, currentUser) {
        feedPosts.filter { it.user.id == currentUser.id }
    }
    val savedPosts = remember(feedPosts) {
        feedPosts.filter { it.isSaved }
    }
    val reelsPosts = remember(myPosts) {
        myPosts.filter { it.type == PostType.VIDEO }
    }

    val displayGridPosts = when (activeTab) {
        ProfileTab.POSTS -> myPosts
        ProfileTab.REELS -> if (reelsPosts.isNotEmpty()) reelsPosts else myPosts
        ProfileTab.SAVED -> savedPosts
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentUser.username,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(status = currentUser.verification, size = 16.dp)
                    }
                },
                actions = {
                    if (currentUser.role == UserRole.ADMIN) {
                        IconButton(
                            onClick = { viewModel.navigateTo(NavigationDestination.ADMIN_PANEL) },
                            modifier = Modifier.testTag("profile_admin_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Panel",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(NavigationDestination.SETTINGS_PRIVACY) },
                        modifier = Modifier.testTag("profile_settings_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Avatar + Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentUser.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentUser.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(count = "${currentUser.postsCount}", label = "Posts")
                        ProfileStatItem(count = "${currentUser.followersCount}", label = "Followers")
                        ProfileStatItem(count = "${currentUser.followingCount}", label = "Following")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bio & Details
                Text(
                    text = currentUser.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (currentUser.bio.isNotBlank()) {
                    Text(
                        text = currentUser.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (currentUser.website.isNotBlank()) {
                    Text(
                        text = currentUser.website,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LykoVioletPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons (Edit Profile / Share Profile)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_profile_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Text(
                            text = "Edit Profile",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { viewModel.showMessage("Profile link copied to clipboard!") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Text(
                            text = "Share Profile",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Profile Tabs (Posts | Reels | Saved)
            TabRow(
                selectedTabIndex = activeTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = LykoVioletPrimary
            ) {
                Tab(
                    selected = activeTab == ProfileTab.POSTS,
                    onClick = { activeTab = ProfileTab.POSTS },
                    icon = { Icon(Icons.Default.GridOn, contentDescription = "Posts") }
                )
                Tab(
                    selected = activeTab == ProfileTab.REELS,
                    onClick = { activeTab = ProfileTab.REELS },
                    icon = { Icon(Icons.Default.PlayCircle, contentDescription = "Reels") }
                )
                Tab(
                    selected = activeTab == ProfileTab.SAVED,
                    onClick = { activeTab = ProfileTab.SAVED },
                    icon = { Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved") }
                )
            }

            // Grid Content
            if (displayGridPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (activeTab) {
                            ProfileTab.POSTS -> "No posts published yet"
                            ProfileTab.REELS -> "No reels posted yet"
                            ProfileTab.SAVED -> "No saved posts yet"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(displayGridPosts) { post ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(post.mediaUrls.firstOrNull().orEmpty())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = post.caption,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (post.type == PostType.VIDEO) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Modal
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(currentUser.displayName) }
        var editBio by remember { mutableStateOf(currentUser.bio) }
        var editWebsite by remember { mutableStateOf(currentUser.website) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = editWebsite,
                        onValueChange = { editWebsite = it },
                        label = { Text("Website") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            displayName = editName,
                            bio = editBio,
                            website = editWebsite,
                            avatarUrl = currentUser.avatarUrl
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LykoVioletPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
