package com.example.ui.screens.live

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.LykoCyanTertiary
import com.example.ui.theme.LykoMagentaSecondary
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FloatingParticle(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val emoji: String = listOf("❤️", "🔥", "💎", "✨", "🚀", "👏").random(),
    val offsetX: Float = Random.nextFloat() * 60f - 30f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamRoomScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    val activeStreamId by viewModel.activeLiveStreamId.collectAsState()
    val liveStreams by viewModel.liveStreams.collectAsState()
    val currentStream = liveStreams.firstOrNull { it.id == activeStreamId } ?: return

    val comments by viewModel.getCommentsForLive(currentStream.id).collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var showGiftsSheet by remember { mutableStateOf(false) }
    var floatingParticles by remember { mutableStateOf(listOf<FloatingParticle>()) }
    val coroutineScope = rememberCoroutineScope()

    // Host follow status
    val isFollowingHost = currentStream.host.isFollowing

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Stream Video Background
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(currentStream.previewThumbnail)
                .crossfade(true)
                .build(),
            contentDescription = currentStream.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Shading
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Stream Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Streamer Info Pill
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Black.copy(alpha = 0.55f),
                modifier = Modifier.clickable { viewModel.openUserProfile(currentStream.host.id) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentStream.host.avatarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = currentStream.host.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentStream.host.displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedBadge(status = currentStream.host.verification, size = 10.dp)
                        }
                        Text(
                            text = "${currentStream.viewersCount} watching",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }

                    if (currentStream.host.id != currentUser.id) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isFollowingHost) Color.White.copy(alpha = 0.2f) else LykoVioletPrimary,
                            modifier = Modifier.clickable { viewModel.toggleFollowUser(currentStream.host.id) }
                        ) {
                            Text(
                                text = if (isFollowingHost) "Following" else "Follow",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Right Badges (Live Pulse + Close)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Red
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.navigateTo(NavigationDestination.LIVE_BROWSE) },
                    modifier = Modifier.testTag("close_live_stream_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit Live Stream",
                        tint = Color.White
                    )
                }
            }
        }

        // Live Chat Messages (Bottom Left)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.72f)
                .padding(start = 14.dp, bottom = 74.dp)
        ) {
            // Stream Title Sticker
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "🔴 ${currentStream.title}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Message stream
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                reverseLayout = false
            ) {
                items(comments) { comment ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${comment.user.displayName}: ",
                                color = LykoCyanTertiary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = comment.content,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Floating reaction particle container
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp)
                .size(width = 80.dp, height = 240.dp)
        ) {
            floatingParticles.forEach { particle ->
                val particleAnim = remember { Animatable(0f) }
                LaunchedEffect(particle.id) {
                    particleAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(1500, easing = FastOutSlowInEasing)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(
                            x = (particle.offsetX * particleAnim.value).dp,
                            y = (-200 * particleAnim.value).dp
                        )
                ) {
                    Text(
                        text = particle.emoji,
                        fontSize = 28.sp
                    )
                }
            }
        }

        // Bottom Controls Row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Comment live...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("live_chat_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.2f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Send comment button
            if (chatInput.isNotBlank()) {
                IconButton(
                    onClick = {
                        viewModel.addCommentToLive(currentStream.id, chatInput)
                        chatInput = ""
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LykoVioletPrimary)
                        .testTag("send_live_comment_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Virtual Gifts Tip Button
            IconButton(
                onClick = { showGiftsSheet = true },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LykoMagentaSecondary)
                    .testTag("live_gift_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Send Gift",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Heart Burst Reaction Button
            IconButton(
                onClick = {
                    val newParticle = FloatingParticle()
                    floatingParticles = (floatingParticles + newParticle).takeLast(10)
                    coroutineScope.launch {
                        delay(1600)
                        floatingParticles = floatingParticles.filterNot { it.id == newParticle.id }
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
                    .testTag("live_heart_burst_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Burst Heart",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }

    // Virtual Gifts Bottom Sheet
    if (showGiftsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showGiftsSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Tip Creator with Virtual Gifts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Support ${currentStream.host.displayName}'s stream live!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                val gifts = listOf(
                    Triple("🌹 Rose", "10 Gems", "🌹"),
                    Triple("💎 Diamond", "50 Gems", "💎"),
                    Triple("🚀 Rocket Launch", "100 Gems", "🚀"),
                    Triple("👑 Golden Crown", "500 Gems", "👑")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    gifts.forEach { (name, cost, emoji) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    showGiftsSheet = false
                                    viewModel.addCommentToLive(currentStream.id, "Sent a gift: $emoji $name ($cost)!")
                                    viewModel.showMessage("Gift sent to ${currentStream.host.displayName}!")
                                }
                                .padding(10.dp)
                        ) {
                            Text(emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(name.split(" ").last(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(cost, fontSize = 10.sp, color = LykoVioletPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
