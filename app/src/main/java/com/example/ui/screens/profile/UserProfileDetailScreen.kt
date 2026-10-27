package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.components.ReportDialog
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val selectedUserId by viewModel.selectedUserProfileId.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val feedPosts by viewModel.feedPosts.collectAsState()
    val reportTarget by viewModel.reportTarget.collectAsState()

    val targetUser = allUsers.firstOrNull { it.id == selectedUserId } ?: return
    val userPosts = remember(feedPosts, targetUser) {
        feedPosts.filter { it.user.id == targetUser.id }
    }

    var showOptionsMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = targetUser.username,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(status = targetUser.verification, size = 14.dp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(NavigationDestination.FEED) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(targetUser.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = targetUser.displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(count = "${targetUser.postsCount}", label = "Posts")
                        ProfileStat(count = "${targetUser.followersCount}", label = "Followers")
                        ProfileStat(count = "${targetUser.followingCount}", label = "Following")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = targetUser.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (targetUser.bio.isNotBlank()) {
                    Text(
                        text = targetUser.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (targetUser.website.isNotBlank()) {
                    Text(
                        text = targetUser.website,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LykoVioletPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Follow / Message Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleFollowUser(targetUser.id) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("user_follow_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (targetUser.isFollowing) MaterialTheme.colorScheme.surfaceContainer else LykoVioletPrimary
                        )
                    ) {
                        Text(
                            text = if (targetUser.isFollowing) "Following" else "Follow",
                            color = if (targetUser.isFollowing) MaterialTheme.colorScheme.onSurface else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val threadId = viewModel.startOrGetDirectChatWith(targetUser.id)
                            viewModel.openChatThread(threadId)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("user_message_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Text(
                            text = "Message",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Posts Grid
            if (userPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posts yet by @${targetUser.username}",
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
                    items(userPosts) { post ->
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

    // User Options Bottom Sheet (Report / Block)
    if (showOptionsMenu) {
        ModalBottomSheet(
            onDismissRequest = { showOptionsMenu = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "@${targetUser.username}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                ListItem(
                    headlineContent = { Text("Report account", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        showOptionsMenu = false
                        viewModel.openReportDialog(targetUser.id, "user")
                    }
                )

                ListItem(
                    headlineContent = {
                        Text(
                            if (targetUser.isBlocked) "Unblock user" else "Block user",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingContent = { Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        showOptionsMenu = false
                        viewModel.toggleBlockUser(targetUser.id)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Report Dialog
    reportTarget?.let { target ->
        ReportDialog(
            targetType = target.second,
            onDismiss = { viewModel.closeReportDialog() },
            onSubmit = { reason -> viewModel.submitReport(reason) }
        )
    }
}

@Composable
private fun ProfileStat(count: String, label: String) {
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
