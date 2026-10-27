package com.example.ui.screens.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Post
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.LykoTopBar
import com.example.ui.components.PostCard
import com.example.ui.components.ReportDialog
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.StoryAvatar
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.FeedTab
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val stories by viewModel.stories.collectAsState()
    val feedPosts by viewModel.feedPosts.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val selectedTab by viewModel.selectedFeedTab.collectAsState()
    val activeCommentPostId by viewModel.activeCommentPostId.collectAsState()
    val activeSharePost by viewModel.activeSharePost.collectAsState()
    val reportTarget by viewModel.reportTarget.collectAsState()

    var postForOptions by remember { mutableStateOf<Post?>(null) }

    val filteredPosts = remember(feedPosts, selectedTab) {
        when (selectedTab) {
            FeedTab.FOR_YOU -> feedPosts
            FeedTab.FOLLOWING -> feedPosts.filter { it.user.isFollowing || it.user.id == currentUser.id }
            FeedTab.EXPLORE -> feedPosts.filter { !it.isSponsored }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LykoTopBar(
                unreadNotificationsCount = 2,
                unreadMessagesCount = 1,
                onNotificationsClick = { viewModel.navigateTo(NavigationDestination.NOTIFICATIONS) },
                onMessagesClick = { viewModel.navigateTo(NavigationDestination.MESSAGES) },
                onLiveClick = { viewModel.navigateTo(NavigationDestination.LIVE_BROWSE) },
                onAdminClick = { viewModel.navigateTo(NavigationDestination.ADMIN_PANEL) },
                isAdmin = currentUser.role == com.example.data.model.UserRole.ADMIN
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Stories Header Row
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Own Story item
                    item {
                        StoryAvatar(
                            imageUrl = currentUser.avatarUrl,
                            username = currentUser.displayName,
                            isMyStory = true,
                            hasStory = stories.any { it.userId == currentUser.id },
                            isSeen = false,
                            onClick = {
                                val index = stories.indexOfFirst { it.userId == currentUser.id }
                                if (index != -1) viewModel.openStory(index) else viewModel.navigateTo(NavigationDestination.CREATE)
                            },
                            onAddStoryClick = { viewModel.navigateTo(NavigationDestination.CREATE) }
                        )
                    }

                    // Friends stories
                    itemsIndexed(stories.filterNot { it.userId == currentUser.id }) { index, story ->
                        StoryAvatar(
                            imageUrl = story.user.avatarUrl,
                            username = story.user.displayName,
                            isSeen = story.isSeen,
                            hasStory = true,
                            onClick = {
                                val actualIdx = stories.indexOf(story)
                                viewModel.openStory(actualIdx)
                            }
                        )
                    }
                }
            }

            // Feed Tabs Switcher (For You | Following)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            FeedTabPill(
                                label = "For You",
                                selected = selectedTab == FeedTab.FOR_YOU,
                                onClick = { viewModel.setFeedTab(FeedTab.FOR_YOU) }
                            )
                            FeedTabPill(
                                label = "Following",
                                selected = selectedTab == FeedTab.FOLLOWING,
                                onClick = { viewModel.setFeedTab(FeedTab.FOLLOWING) }
                            )
                        }
                    }
                }
            }

            // Posts Stream
            if (filteredPosts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No posts in this feed yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.setFeedTab(FeedTab.FOR_YOU) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Discover creators")
                            }
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        onLikeClick = { viewModel.toggleLikePost(post.id) },
                        onCommentClick = { viewModel.openCommentsFor(post.id) },
                        onShareClick = { viewModel.openShareSheet(post) },
                        onSaveClick = { viewModel.toggleSavePost(post.id) },
                        onUserClick = { userId -> viewModel.openUserProfile(userId) },
                        onOptionsClick = { postForOptions = it }
                    )
                }
            }
        }
    }

    // Comments Sheet
    activeCommentPostId?.let { postId ->
        val comments by viewModel.getCommentsForPost(postId).collectAsState()
        CommentsBottomSheet(
            comments = comments,
            currentUserAvatar = currentUser.avatarUrl,
            onDismiss = { viewModel.closeComments() },
            onAddComment = { text -> viewModel.addComment(postId, text) }
        )
    }

    // Share Sheet
    activeSharePost?.let { post ->
        val friendList = remember(allUsers) { allUsers.filterNot { it.id == currentUser.id } }
        ShareBottomSheet(
            post = post,
            friends = friendList,
            onDismiss = { viewModel.closeShareSheet() },
            onSendToUser = { user ->
                viewModel.showMessage("Sent post to ${user.displayName} in direct messages")
                viewModel.closeShareSheet()
            },
            onCopyLink = {
                viewModel.showMessage("Post link copied to clipboard")
                viewModel.closeShareSheet()
            },
            onShareToStory = {
                viewModel.createStory(post.mediaUrls.firstOrNull().orEmpty(), "Shared: ${post.caption.take(40)}...")
                viewModel.closeShareSheet()
            },
            onShareExternal = {
                viewModel.showMessage("Opening system share sheet...")
                viewModel.closeShareSheet()
            }
        )
    }

    // Post Options Modal Sheet
    postForOptions?.let { post ->
        ModalBottomSheet(
            onDismissRequest = { postForOptions = null },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Post Options",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                ListItem(
                    headlineContent = { Text("Save to collection") },
                    leadingContent = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                    modifier = Modifier.clickable {
                        viewModel.toggleSavePost(post.id)
                        postForOptions = null
                    }
                )

                ListItem(
                    headlineContent = { Text("Copy link") },
                    leadingContent = { Icon(Icons.Default.Link, contentDescription = null) },
                    modifier = Modifier.clickable {
                        viewModel.showMessage("Link copied to clipboard")
                        postForOptions = null
                    }
                )

                ListItem(
                    headlineContent = { Text("Report post", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Default.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        viewModel.openReportDialog(post.id, "post")
                        postForOptions = null
                    }
                )

                if (post.user.id != currentUser.id) {
                    ListItem(
                        headlineContent = { Text("Block @${post.user.username}", color = MaterialTheme.colorScheme.error) },
                        leadingContent = { Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                        modifier = Modifier.clickable {
                            viewModel.toggleBlockUser(post.user.id)
                            postForOptions = null
                        }
                    )
                }

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
private fun FeedTabPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) LykoVioletPrimary else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}
