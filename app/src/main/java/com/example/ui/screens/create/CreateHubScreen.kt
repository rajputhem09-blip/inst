package com.example.ui.screens.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.PostType
import com.example.ui.theme.LykoGradientBrush
import com.example.ui.theme.LykoLiveGradient
import com.example.ui.theme.LykoMagentaSecondary
import com.example.ui.theme.LykoVioletPrimary
import com.example.ui.viewmodel.LykoViewModel
import com.example.ui.viewmodel.NavigationDestination

enum class CreationMode {
    POST,
    STORY,
    GO_LIVE
}

@Composable
fun CreateHubScreen(
    viewModel: LykoViewModel,
    modifier: Modifier = Modifier
) {
    var activeMode by remember { mutableStateOf(CreationMode.POST) }

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=800&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop"
    )

    // Post state
    var selectedMediaUrl by remember { mutableStateOf(presetImages[0]) }
    var caption by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Tokyo, Japan") }
    var postType by remember { mutableStateOf(PostType.IMAGE) }
    var isSponsored by remember { mutableStateOf(false) }
    var sponsorCta by remember { mutableStateOf("Shop Collection") }

    // Story state
    var storyMediaUrl by remember { mutableStateOf(presetImages[1]) }
    var storyOverlayText by remember { mutableStateOf("") }

    // Go Live state
    var liveTitle by remember { mutableStateOf("Late Night Audio Mixing & Chill Q&A 🎧") }
    var liveCategory by remember { mutableStateOf("Music & Tech") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Studio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(
                        onClick = { viewModel.navigateTo(NavigationDestination.FEED) }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mode Tabs (Post | Story | Go Live)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CreationModeTab(
                        title = "Post",
                        selected = activeMode == CreationMode.POST,
                        onClick = { activeMode = CreationMode.POST }
                    )
                    CreationModeTab(
                        title = "Story",
                        selected = activeMode == CreationMode.STORY,
                        onClick = { activeMode = CreationMode.STORY }
                    )
                    CreationModeTab(
                        title = "Go Live",
                        selected = activeMode == CreationMode.GO_LIVE,
                        onClick = { activeMode = CreationMode.GO_LIVE }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (activeMode) {
                CreationMode.POST -> {
                    // Media Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.DarkGray)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedMediaUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Selected media",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = if (postType == PostType.VIDEO) "Format: Video Reel" else "Format: Photo Post",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Choose Photo or Video Asset",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Preset assets picker
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetImages) { url ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (selectedMediaUrl == url) 2.5.dp else 0.dp,
                                        color = if (selectedMediaUrl == url) LykoVioletPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedMediaUrl = url }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Caption Input
                    OutlinedTextField(
                        value = caption,
                        onValueChange = { caption = it },
                        label = { Text("Write a caption...") },
                        placeholder = { Text("Share the story behind this moment...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_caption_input"),
                        shape = RoundedCornerShape(14.dp),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tags Input
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        label = { Text("Tags (comma separated)") },
                        placeholder = { Text("cyberpunk, music, vibes, tech") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("create_tags_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location Input
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Add Location") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Post Type Selector (Image / Carousel / Video)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = postType == PostType.IMAGE,
                            onClick = { postType = PostType.IMAGE },
                            label = { Text("Image") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = postType == PostType.VIDEO,
                            onClick = { postType = PostType.VIDEO },
                            label = { Text("Video Reel") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = postType == PostType.CAROUSEL,
                            onClick = { postType = PostType.CAROUSEL },
                            label = { Text("Carousel") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sponsored / Ad Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Paid Partnership / Sponsored",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Show sponsored label with call-to-action button",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isSponsored,
                            onCheckedChange = { isSponsored = it }
                        )
                    }

                    if (isSponsored) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = sponsorCta,
                            onValueChange = { sponsorCta = it },
                            label = { Text("Call to Action Label") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Publish Button
                    Button(
                        onClick = {
                            val parsedTags = tagsInput.split(",").map { it.trim().removePrefix("#") }.filter { it.isNotBlank() }
                            val mediaList = if (postType == PostType.CAROUSEL) {
                                listOf(selectedMediaUrl, presetImages[2], presetImages[3])
                            } else {
                                listOf(selectedMediaUrl)
                            }

                            viewModel.createPost(
                                caption = caption.ifBlank { "Excited to share this with the Lyko community! ✨" },
                                mediaUrls = mediaList,
                                tags = if (parsedTags.isNotEmpty()) parsedTags else listOf("lyko", "trending"),
                                location = location,
                                type = postType,
                                isSponsored = isSponsored,
                                sponsorCtaText = if (isSponsored) sponsorCta else null
                            )
                            viewModel.navigateTo(NavigationDestination.FEED)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("publish_post_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LykoVioletPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share to Feed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                CreationMode.STORY -> {
                    // Story Builder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(storyMediaUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Story asset",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (storyOverlayText.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Black.copy(alpha = 0.65f),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = storyOverlayText,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Choose Story Background",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(presetImages) { url ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (storyMediaUrl == url) 2.5.dp else 0.dp,
                                        color = if (storyMediaUrl == url) LykoMagentaSecondary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { storyMediaUrl = url }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = storyOverlayText,
                        onValueChange = { storyOverlayText = it },
                        label = { Text("Story Text Sticker / Overlay") },
                        placeholder = { Text("What's happening right now? 🔥") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("story_overlay_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.createStory(
                                mediaUrl = storyMediaUrl,
                                textOverlay = storyOverlayText
                            )
                            viewModel.navigateTo(NavigationDestination.FEED)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("publish_story_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LykoMagentaSecondary)
                    ) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Your Story (24h)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                CreationMode.GO_LIVE -> {
                    // Go Live Broadcast Setup
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(LykoLiveGradient)
                            .padding(2.dp),
                        color = Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(18.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Live Broadcast",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = liveTitle,
                                onValueChange = { liveTitle = it },
                                label = { Text("Stream Title") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("live_title_input"),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = liveCategory,
                                onValueChange = { liveCategory = it },
                                label = { Text("Stream Category") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val stream = viewModel.startLiveStream(liveTitle, liveCategory)
                                    viewModel.openLiveStream(stream.id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("start_broadcast_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Go Live Now 🔴",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreationModeTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) MaterialTheme.colorScheme.background else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}
