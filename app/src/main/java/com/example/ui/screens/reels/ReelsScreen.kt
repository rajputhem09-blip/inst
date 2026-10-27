package com.example.ui.screens.reels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Post
import com.example.data.model.PostType
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.ShareBottomSheet
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LykoMagentaSecondary
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ReelsScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val feedPosts by viewModel.feedPosts.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val activeCommentPostId by viewModel.activeCommentPostId.collectAsState()
    val activeSharePost by viewModel.activeSharePost.collectAsState()

    // Filter posts for video or all for immersive reel experience
    val reelPosts = remember(feedPosts) {
        val videoOnes = feedPosts.filter { it.type == PostType.VIDEO }
        if (videoOnes.isNotEmpty()) videoOnes else feedPosts
    }

    val pagerState = rememberPagerState(pageCount = { reelPosts.size })

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val post = reelPosts.getOrNull(page) ?: return@VerticalPager
            SingleReelItem(
                post = post,
                onLikeClick = { viewModel.toggleLikePost(post.id) },
                onCommentClick = { viewModel.openCommentsFor(post.id) },
                onShareClick = { viewModel.openShareSheet(post) },
                onSaveClick = { viewModel.toggleSavePost(post.id) },
                onUserClick = { viewModel.openUserProfile(post.user.id) },
                onFollowToggle = { viewModel.toggleFollowUser(post.user.id) }
            )
        }

        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reels",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            IconButton(
                onClick = { viewModel.navigateTo(NavigationDestination.CREATE) },
                modifier = Modifier.testTag("reels_camera_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White
                )
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
        val friends = remember(allUsers) { allUsers.filterNot { it.id == currentUser.id } }
        ShareBottomSheet(
            post = post,
            friends = friends,
            onDismiss = { viewModel.closeShareSheet() },
            onSendToUser = { user ->
                viewModel.showMessage("Reel sent to ${user.displayName}")
                viewModel.closeShareSheet()
            },
            onCopyLink = {
                viewModel.showMessage("Reel link copied")
                viewModel.closeShareSheet()
            },
            onShareToStory = {
                viewModel.createStory(post.mediaUrls.firstOrNull().orEmpty(), "Reel by @${post.user.username}")
                viewModel.closeShareSheet()
            },
            onShareExternal = {
                viewModel.showMessage("Sharing reel externally...")
                viewModel.closeShareSheet()
            }
        )
    }
}

@Composable
private fun SingleReelItem(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onSaveClick: () -> Unit,
    onUserClick: () -> Unit,
    onFollowToggle: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showBigHeart by remember { mutableStateOf(false) }
    var isExpandedCaption by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Rotating vinyl disc animation
    val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!post.isLiked) onLikeClick()
                        showBigHeart = true
                        coroutineScope.launch {
                            delay(900)
                            showBigHeart = false
                        }
                    },
                    onTap = {
                        isPlaying = !isPlaying
                    }
                )
            }
    ) {
        // Video / Reel Media
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(post.mediaUrls.firstOrNull().orEmpty())
                .crossfade(true)
                .build(),
            contentDescription = post.caption,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Gradients for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Pause state indicator
        if (!isPlaying) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Paused",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Big Double Tap Heart
        AnimatedVisibility(
            visible = showBigHeart,
            modifier = Modifier.align(Alignment.Center),
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = LykoMagentaSecondary,
                modifier = Modifier.size(110.dp)
            )
        }

        // Right Action Bar (Vertical)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier.testTag("reel_like_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) LykoMagentaSecondary else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "${post.likesCount}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Comment Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier.testTag("reel_comment_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = "${post.commentsCount}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Share Button
            IconButton(
                onClick = onShareClick,
                modifier = Modifier.testTag("reel_share_${post.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "Share",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Save Bookmark Button
            IconButton(
                onClick = onSaveClick,
                modifier = Modifier.testTag("reel_save_${post.id}")
            ) {
                Icon(
                    imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Save",
                    tint = if (post.isSaved) LykoVioletPrimary else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Spinning Vinyl Audio Disc
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .rotate(if (isPlaying) rotation else 0f)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.user.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Audio track cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )
            }
        }

        // Bottom Left Creator & Caption Details
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.80f)
                .padding(start = 16.dp, bottom = 28.dp)
        ) {
            // Creator Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onUserClick() }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.user.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = post.user.displayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.user.displayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        VerifiedBadge(status = post.user.verification, size = 12.dp)
                    }
                    Text(
                        text = "@${post.user.username}",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (post.user.isFollowing) Color.White.copy(alpha = 0.2f) else LykoVioletPrimary,
                    modifier = Modifier.clickable { onFollowToggle() }
                ) {
                    Text(
                        text = if (post.user.isFollowing) "Following" else "Follow",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Caption Text
            Text(
                text = post.caption,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = if (isExpandedCaption) 6 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isExpandedCaption = !isExpandedCaption }
            )

            // Tags
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.tags.joinToString(" ") { "#$it" },
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio Track name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio track",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${post.user.displayName} • Original Audio (HQ Stereo)",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }
    }
}
