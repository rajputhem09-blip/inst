package com.example.data.repository

import com.example.data.local.BlockedUserEntity
import com.example.data.local.LykoDao
import com.example.data.local.SavedPostEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class LykoRepository(private val dao: LykoDao? = null) {

    // Current logged-in user
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            username = "alex_vortex",
            displayName = "Alex Vance",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
            bannerUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80",
            bio = "Digital artist & sound designer ⚡ Building visual worlds on Lyko 🌌 #FutureAesthetics",
            website = "https://lyko.social/@alex",
            verification = VerificationStatus.VERIFIED_CREATOR,
            role = UserRole.ADMIN,
            followersCount = 14200,
            followingCount = 384,
            postsCount = 42,
            isFollowing = false
        )
    )
    val currentUser = _currentUser.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    // Users list for discovery & admin
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers = _allUsers.asStateFlow()

    // Stories
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories = _stories.asStateFlow()

    // Posts Feed
    private val _feedPosts = MutableStateFlow<List<Post>>(emptyList())
    val feedPosts = _feedPosts.asStateFlow()

    // Reels
    private val _reels = MutableStateFlow<List<Reel>>(emptyList())
    val reels = _reels.asStateFlow()

    // Chats
    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()

    // Active Chat Messages mapped by ChatId
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages = _messages.asStateFlow()

    // Live Streams
    private val _liveStreams = MutableStateFlow<List<LiveStream>>(emptyList())
    val liveStreams = _liveStreams.asStateFlow()

    // Live Stream Comments mapped by streamId
    private val _liveComments = MutableStateFlow<Map<String, List<LiveComment>>>(emptyMap())
    val liveComments = _liveComments.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications = _notifications.asStateFlow()

    // Admin Reports & Content Moderation
    private val _reports = MutableStateFlow<List<ContentReport>>(emptyList())
    val reports = _reports.asStateFlow()

    // Admin Ad Campaigns
    private val _adCampaigns = MutableStateFlow<List<AdCampaign>>(emptyList())
    val adCampaigns = _adCampaigns.asStateFlow()

    // Post comments map
    private val _postComments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val postComments = _postComments.asStateFlow()

    // Blocked users set
    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds = _blockedUserIds.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val userElena = User(
            id = "user_elena",
            username = "elena_solaris",
            displayName = "Elena Rostova",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
            bannerUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1000&auto=format&fit=crop&q=80",
            bio = "Photographer & travel storyteller 📸 Exploring unseen corners of the world 🏔️",
            website = "https://elenarostova.gallery",
            verification = VerificationStatus.VERIFIED_CREATOR,
            followersCount = 89300,
            followingCount = 520,
            postsCount = 188,
            isFollowing = true
        )

        val userMarcus = User(
            id = "user_marcus",
            username = "marcus_cyber",
            displayName = "Marcus Chen",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
            bannerUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=1000&auto=format&fit=crop&q=80",
            bio = "AI systems engineer & techno music producer 🎧 Live sets every Friday!",
            website = "https://cyberchen.io",
            verification = VerificationStatus.VERIFIED_OFFICIAL,
            followersCount = 124000,
            followingCount = 210,
            postsCount = 95,
            isFollowing = false
        )

        val userZara = User(
            id = "user_zara",
            username = "zara_nova",
            displayName = "Zara K.",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80",
            bannerUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1000&auto=format&fit=crop&q=80",
            bio = "High fashion & streetwear curator 👗 Style without limits 💎",
            website = "https://zaranova.style",
            verification = VerificationStatus.VERIFIED_CREATOR,
            followersCount = 45100,
            followingCount = 612,
            postsCount = 310,
            isFollowing = true
        )

        val userKai = User(
            id = "user_kai",
            username = "kai_chronicles",
            displayName = "Kai Tanaka",
            avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
            bannerUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1000&auto=format&fit=crop&q=80",
            bio = "Game Developer & Unreal Engine cinematic creator 🎮 Tokyo, JP",
            website = "https://kaistudio.dev",
            verification = VerificationStatus.VERIFIED_CREATOR,
            followersCount = 67200,
            followingCount = 189,
            postsCount = 145,
            isFollowing = false
        )

        val userLykoOfficial = User(
            id = "user_lyko_official",
            username = "lyko",
            displayName = "Lyko Official",
            avatarUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500&auto=format&fit=crop&q=80",
            bio = "The next generation creative social network. Build, connect, and thrive.",
            website = "https://lyko.social",
            verification = VerificationStatus.VERIFIED_ADMIN,
            role = UserRole.ADMIN,
            followersCount = 1500000,
            followingCount = 5,
            postsCount = 300,
            isFollowing = true
        )

        val userSuspicious = User(
            id = "user_spambot_99",
            username = "crypto_winner_998",
            displayName = "Win Free Coins Now",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=500&auto=format&fit=crop&q=80",
            bio = "Click the link to claim 5000 USDT reward instantly!",
            verification = VerificationStatus.NONE,
            role = UserRole.USER,
            followersCount = 12,
            followingCount = 1500,
            postsCount = 8,
            isBanned = false
        )

        _allUsers.value = listOf(_currentUser.value, userElena, userMarcus, userZara, userKai, userLykoOfficial, userSuspicious)

        // Stories
        _stories.value = listOf(
            Story(
                id = "story_me",
                userId = _currentUser.value.id,
                user = _currentUser.value,
                mediaUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80",
                textOverlay = "Setting up my audio booth for tonight 🎙️⚡",
                isSeen = true,
                likesCount = 28
            ),
            Story(
                id = "story_1",
                userId = userElena.id,
                user = userElena,
                mediaUrl = "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&auto=format&fit=crop&q=80",
                textOverlay = "Sunrise in the Dolomites 🌅 Golden hour glow",
                isSeen = false,
                likesCount = 142
            ),
            Story(
                id = "story_2",
                userId = userMarcus.id,
                user = userMarcus,
                mediaUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
                textOverlay = "Modular synth jam session live at 9PM! 🎛️",
                isSeen = false,
                likesCount = 310
            ),
            Story(
                id = "story_3",
                userId = userZara.id,
                user = userZara,
                mediaUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=800&auto=format&fit=crop&q=80",
                textOverlay = "Paris Fashion Week preview 🖤",
                isSeen = false,
                likesCount = 980
            ),
            Story(
                id = "story_4",
                userId = userKai.id,
                user = userKai,
                mediaUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop&q=80",
                textOverlay = "Rendering realtime lighting pass in UE5 🔥",
                isSeen = true,
                likesCount = 450
            )
        )

        // Feed posts
        _feedPosts.value = listOf(
            Post(
                id = "post_1",
                userId = userElena.id,
                user = userElena,
                type = PostType.CAROUSEL,
                mediaUrls = listOf(
                    "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1000&auto=format&fit=crop&q=80",
                    "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=1000&auto=format&fit=crop&q=80"
                ),
                caption = "Lost in the mystical valleys of Norway. The reflections on the fjord were like pure liquid glass. Which slide is your favorite? 🌲✨",
                tags = listOf("Norway", "Landscape", "Photography", "LykoVibes"),
                location = "Geirangerfjord, Norway",
                likesCount = 3420,
                commentsCount = 184,
                sharesCount = 512,
                isLiked = true,
                isSaved = true
            ),
            Post(
                id = "post_ad_1",
                userId = userLykoOfficial.id,
                user = userLykoOfficial,
                type = PostType.ADVERTISEMENT,
                mediaUrls = listOf("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1000&auto=format&fit=crop&q=80"),
                caption = "Level up your audio workspace with StudioX Acoustics. Pure low-latency hardware for next-gen creators. 🎧🔥",
                tags = listOf("StudioGear", "Sponsored", "SoundDesign"),
                location = "Promoted by StudioX",
                likesCount = 890,
                commentsCount = 42,
                sharesCount = 120,
                isSponsored = true,
                sponsorCtaText = "Claim 20% Off",
                sponsorCtaUrl = "https://example.com/studiox"
            ),
            Post(
                id = "post_2",
                userId = userMarcus.id,
                user = userMarcus,
                type = PostType.VIDEO,
                mediaUrls = listOf("https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1000&auto=format&fit=crop&q=80"),
                caption = "Live audio synthesis experiment blending analog filters with generative neural beats. Turn your volume up for this drop! 🔊⚡",
                tags = listOf("SynthWave", "Cyberpunk", "MusicProduction"),
                location = "Berlin Sound Lab",
                likesCount = 7810,
                commentsCount = 420,
                sharesCount = 1390,
                isLiked = false,
                isSaved = false
            ),
            Post(
                id = "post_3",
                userId = userZara.id,
                user = userZara,
                type = PostType.IMAGE,
                mediaUrls = listOf("https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=1000&auto=format&fit=crop&q=80"),
                caption = "Monochrome minimalism with architectural tailoring. Fall collection editorial mood board. 🤍🖤",
                tags = listOf("HighFashion", "Editorial", "Minimalist"),
                location = "Milan, Italy",
                likesCount = 5120,
                commentsCount = 290,
                sharesCount = 840,
                isLiked = false,
                isSaved = false
            )
        )

        // Seed Comments
        _postComments.value = mapOf(
            "post_1" to listOf(
                Comment(
                    postId = "post_1",
                    user = userMarcus,
                    content = "The lighting in the second slide is absolutely breathtaking! 🔥",
                    likesCount = 42,
                    isLiked = true
                ),
                Comment(
                    postId = "post_1",
                    user = userZara,
                    content = "Pure magic Elena! Need your camera settings for this shot 📸",
                    likesCount = 19,
                    isLiked = false
                ),
                Comment(
                    postId = "post_1",
                    user = _currentUser.value,
                    content = "Incredible perspective. Reminds me of atmospheric film cinematography.",
                    likesCount = 8,
                    isLiked = false
                )
            ),
            "post_2" to listOf(
                Comment(
                    postId = "post_2",
                    user = userKai,
                    content = "That low-end resonance at 0:24 is filthy! What synth plugin is that?",
                    likesCount = 65,
                    isLiked = true
                )
            )
        )

        // Seed Reels
        _reels.value = listOf(
            Reel(
                id = "reel_1",
                userId = userMarcus.id,
                user = userMarcus,
                videoUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=1080&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=1080&auto=format&fit=crop&q=80",
                caption = "When the modular patch locks into an infinite harmonic rhythm 🎛️🌌 #synthesizer #sounddesign #drop",
                audioTrackName = "Neon Overdrive (Original Mix)",
                audioAuthor = "Marcus Chen",
                likesCount = 48900,
                commentsCount = 1230,
                sharesCount = 8450,
                isLiked = true
            ),
            Reel(
                id = "reel_2",
                userId = userElena.id,
                user = userElena,
                videoUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
                caption = "Chasing waterfalls in Iceland at midnight sun 🌊🏔️ Truly another planet.",
                audioTrackName = "Echoes of the North",
                audioAuthor = "Elena Rostova",
                likesCount = 82400,
                commentsCount = 2100,
                sharesCount = 14200,
                isLiked = false
            ),
            Reel(
                id = "reel_3",
                userId = userZara.id,
                user = userZara,
                videoUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1080&auto=format&fit=crop&q=80",
                thumbnailUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=1080&auto=format&fit=crop&q=80",
                caption = "3 ways to style an oversized chrome trenchcoat ⚡ Streetwear lookbook 2026",
                audioTrackName = "Cyber Runway - Zara Beats",
                audioAuthor = "Zara K.",
                likesCount = 31200,
                commentsCount = 870,
                sharesCount = 4900,
                isLiked = false
            )
        )

        // Seed Chat Rooms
        val chat1Id = "chat_elena"
        val chat2Id = "chat_marcus"
        val chatGroupId = "chat_group_creators"

        val chatElena = ChatRoom(
            id = chat1Id,
            isGroup = false,
            title = "Elena Rostova",
            avatarUrl = userElena.avatarUrl,
            participants = listOf(_currentUser.value, userElena),
            lastMessage = Message(
                chatId = chat1Id,
                senderId = userElena.id,
                senderName = userElena.displayName,
                senderAvatar = userElena.avatarUrl,
                type = MessageType.VOICE,
                content = "Voice message (0:24)",
                voiceDurationSeconds = 24,
                timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
                isRead = false,
                isEncrypted = true
            ),
            unreadCount = 1,
            isEncrypted = true
        )

        val chatMarcus = ChatRoom(
            id = chat2Id,
            isGroup = false,
            title = "Marcus Chen",
            avatarUrl = userMarcus.avatarUrl,
            participants = listOf(_currentUser.value, userMarcus),
            lastMessage = Message(
                chatId = chat2Id,
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.displayName,
                senderAvatar = _currentUser.value.avatarUrl,
                type = MessageType.TEXT,
                content = "I sent over the stems for the new track. Let me know what you think!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                isRead = true,
                isEncrypted = true
            ),
            unreadCount = 0,
            isEncrypted = true
        )

        val chatGroup = ChatRoom(
            id = chatGroupId,
            isGroup = true,
            title = "🌌 Lyko Creator Syndicate",
            avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=500&auto=format&fit=crop&q=80",
            participants = listOf(_currentUser.value, userElena, userMarcus, userZara, userKai),
            lastMessage = Message(
                chatId = chatGroupId,
                senderId = userZara.id,
                senderName = userZara.displayName,
                senderAvatar = userZara.avatarUrl,
                type = MessageType.TEXT,
                content = "Collaborative live stream session scheduled for Saturday! 🎉",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
                isRead = true,
                isEncrypted = true
            ),
            unreadCount = 0,
            isEncrypted = true
        )

        _chatRooms.value = listOf(chatElena, chatMarcus, chatGroup)

        _messages.value = mapOf(
            chat1Id to listOf(
                Message(
                    chatId = chat1Id,
                    senderId = _currentUser.value.id,
                    senderName = _currentUser.value.displayName,
                    senderAvatar = _currentUser.value.avatarUrl,
                    type = MessageType.TEXT,
                    content = "Hey Elena! Love your new Norway series. What lens were you using on that second shot?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                    isRead = true,
                    isEncrypted = true
                ),
                Message(
                    chatId = chat1Id,
                    senderId = userElena.id,
                    senderName = userElena.displayName,
                    senderAvatar = userElena.avatarUrl,
                    type = MessageType.TEXT,
                    content = "Thanks Alex! It was a 24mm f/1.4 GM with a circular polarizer. Let me send a quick voice note about the color grading.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                    isRead = true,
                    isEncrypted = true
                ),
                Message(
                    chatId = chat1Id,
                    senderId = userElena.id,
                    senderName = userElena.displayName,
                    senderAvatar = userElena.avatarUrl,
                    type = MessageType.VOICE,
                    content = "Voice note (0:24)",
                    voiceDurationSeconds = 24,
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
                    isRead = false,
                    isEncrypted = true
                )
            ),
            chat2Id to listOf(
                Message(
                    chatId = chat2Id,
                    senderId = userMarcus.id,
                    senderName = userMarcus.displayName,
                    senderAvatar = userMarcus.avatarUrl,
                    type = MessageType.TEXT,
                    content = "Are you ready to test the live streaming studio feature on Lyko?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 60,
                    isRead = true,
                    isEncrypted = true
                ),
                Message(
                    chatId = chat2Id,
                    senderId = _currentUser.value.id,
                    senderName = _currentUser.value.displayName,
                    senderAvatar = _currentUser.value.avatarUrl,
                    type = MessageType.TEXT,
                    content = "I sent over the stems for the new track. Let me know what you think!",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    isRead = true,
                    isEncrypted = true
                )
            )
        )

        // Seed Live Streams
        _liveStreams.value = listOf(
            LiveStream(
                id = "live_1",
                host = userMarcus,
                title = "⚡ Live Synthesizer Improvisation & Cyber Beats Q&A",
                category = "Electronic Music",
                viewerCount = 1420,
                likesCount = 8900,
                isLive = true,
                bannerUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=1000&auto=format&fit=crop&q=80"
            ),
            LiveStream(
                id = "live_2",
                host = userZara,
                title = "✨ Runway Unboxing & Styling Luxury Winter Drops",
                category = "Fashion & Design",
                viewerCount = 3890,
                likesCount = 24500,
                isLive = true,
                bannerUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=1000&auto=format&fit=crop&q=80"
            )
        )

        _liveComments.value = mapOf(
            "live_1" to listOf(
                LiveComment(user = userElena, message = "The rhythm switch up is insane! 🔥🔥"),
                LiveComment(user = userKai, message = "Sending 500 Lyko Gems 💎 Keep it up Marcus!", isDonation = true, donationAmount = "$5.00"),
                LiveComment(user = _currentUser.value, message = "Those analog filter sweeps sound so clean!"),
                LiveComment(user = userZara, message = "Vibing in the studio right now ✨🙌")
            )
        )

        // Notifications
        _notifications.value = listOf(
            NotificationItem(
                user = userElena,
                actionType = "liked",
                targetPreview = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=200&auto=format&fit=crop&q=80",
                message = "liked your story.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                isRead = false
            ),
            NotificationItem(
                user = userMarcus,
                actionType = "commented",
                targetPreview = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=200&auto=format&fit=crop&q=80",
                message = "commented: 'The lighting in the second slide is absolutely breathtaking!'",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                isRead = false
            ),
            NotificationItem(
                user = userZara,
                actionType = "followed",
                message = "started following you.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 180,
                isRead = true
            ),
            NotificationItem(
                user = userMarcus,
                actionType = "live",
                message = "is now LIVE: '⚡ Live Synthesizer Improvisation'",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 240,
                isRead = true
            )
        )

        // Admin Reports
        _reports.value = listOf(
            ContentReport(
                id = "report_1",
                reporterId = userZara.id,
                reporterName = userZara.displayName,
                reportedItemId = userSuspicious.id,
                itemType = "user",
                reportedContentSnippet = "Spam bot sending malicious crypto giveaway links in direct messages and comments.",
                reason = "Spam & Phishing",
                status = "Pending"
            ),
            ContentReport(
                id = "report_2",
                reporterId = userElena.id,
                reporterName = userElena.displayName,
                reportedItemId = "post_unauthorized_copy",
                itemType = "post",
                reportedContentSnippet = "Uncredited commercial reproduction of Norway fjord photography.",
                reason = "Copyright Violation",
                status = "Pending"
            )
        )

        // Ad Campaigns
        _adCampaigns.value = listOf(
            AdCampaign(
                id = "ad_1",
                brandName = "StudioX Acoustics",
                brandLogoUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&auto=format&fit=crop&q=80",
                headline = "Next-Gen Low Latency Monitors",
                mediaUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80",
                ctaText = "Shop 20% Off",
                impressions = 48200,
                clicks = 3410,
                budget = "$850.00",
                isActive = true
            ),
            AdCampaign(
                id = "ad_2",
                brandName = "NeoVogue Apparel",
                brandLogoUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=200&auto=format&fit=crop&q=80",
                headline = "Cyberpunk Streetwear 2026",
                mediaUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=800&auto=format&fit=crop&q=80",
                ctaText = "Explore Collection",
                impressions = 62100,
                clicks = 5120,
                budget = "$1,200.00",
                isActive = true
            )
        )
    }

    // --- Actions ---

    fun toggleLikePost(postId: String) {
        _feedPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    val newLikes = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
                    post.copy(isLiked = newLiked, likesCount = newLikes)
                } else post
            }
        }
    }

    fun toggleSavePost(postId: String) {
        _feedPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(isSaved = !post.isSaved)
                } else post
            }
        }
    }

    fun toggleFollowUser(userId: String) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    val newFollowing = !user.isFollowing
                    val newCount = if (newFollowing) user.followersCount + 1 else (user.followersCount - 1).coerceAtLeast(0)
                    user.copy(isFollowing = newFollowing, followersCount = newCount)
                } else user
            }
        }

        // Update posts authored by this user
        _feedPosts.update { list ->
            list.map { post ->
                if (post.user.id == userId) {
                    val updatedUser = _allUsers.value.find { it.id == userId } ?: post.user
                    post.copy(user = updatedUser)
                } else post
            }
        }
    }

    fun addComment(postId: String, content: String) {
        if (content.isBlank()) return
        val newComment = Comment(
            postId = postId,
            user = _currentUser.value,
            content = content.trim(),
            timestamp = System.currentTimeMillis()
        )
        _postComments.update { current ->
            val list = current[postId] ?: emptyList()
            current + (postId to (list + newComment))
        }
        _feedPosts.update { list ->
            list.map { post ->
                if (post.id == postId) {
                    post.copy(commentsCount = post.commentsCount + 1)
                } else post
            }
        }
    }

    fun toggleLikeReel(reelId: String) {
        _reels.update { list ->
            list.map { reel ->
                if (reel.id == reelId) {
                    val newLiked = !reel.isLiked
                    val newLikes = if (newLiked) reel.likesCount + 1 else (reel.likesCount - 1).coerceAtLeast(0)
                    reel.copy(isLiked = newLiked, likesCount = newLikes)
                } else reel
            }
        }
    }

    fun createPost(caption: String, mediaUrls: List<String>, tags: List<String>, location: String) {
        val newPost = Post(
            userId = _currentUser.value.id,
            user = _currentUser.value,
            type = if (mediaUrls.size > 1) PostType.CAROUSEL else PostType.IMAGE,
            mediaUrls = mediaUrls.ifEmpty { listOf("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1000&auto=format&fit=crop&q=80") },
            caption = caption,
            tags = tags,
            location = location,
            timestamp = System.currentTimeMillis()
        )
        _feedPosts.update { listOf(newPost) + it }
        _currentUser.update { it.copy(postsCount = it.postsCount + 1) }
    }

    fun createStory(mediaUrl: String, textOverlay: String) {
        val newStory = Story(
            userId = _currentUser.value.id,
            user = _currentUser.value,
            mediaUrl = mediaUrl.ifBlank { "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop&q=80" },
            textOverlay = textOverlay,
            timestamp = System.currentTimeMillis()
        )
        _stories.update { listOf(newStory) + it.filterNot { s -> s.userId == _currentUser.value.id } }
    }

    fun sendMessage(chatId: String, text: String, messageType: MessageType = MessageType.TEXT, voiceDuration: Int = 0) {
        val msg = Message(
            chatId = chatId,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.displayName,
            senderAvatar = _currentUser.value.avatarUrl,
            type = messageType,
            content = text,
            voiceDurationSeconds = voiceDuration,
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isEncrypted = true
        )
        _messages.update { current ->
            val list = current[chatId] ?: emptyList()
            current + (chatId to (list + msg))
        }
        _chatRooms.update { rooms ->
            rooms.map { room ->
                if (room.id == chatId) {
                    room.copy(lastMessage = msg, updatedAt = System.currentTimeMillis())
                } else room
            }
        }
    }

    fun createGroupChat(title: String, participantIds: List<String>) {
        val selectedUsers = _allUsers.value.filter { participantIds.contains(it.id) }
        val newRoom = ChatRoom(
            id = "chat_group_" + UUID.randomUUID().toString().take(8),
            isGroup = true,
            title = title,
            avatarUrl = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=500&auto=format&fit=crop&q=80",
            participants = listOf(_currentUser.value) + selectedUsers,
            lastMessage = Message(
                chatId = "group",
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.displayName,
                senderAvatar = _currentUser.value.avatarUrl,
                type = MessageType.TEXT,
                content = "Group created: $title",
                timestamp = System.currentTimeMillis()
            ),
            unreadCount = 0,
            isEncrypted = true
        )
        _chatRooms.update { listOf(newRoom) + it }
    }

    fun addLiveComment(streamId: String, text: String, isDonation: Boolean = false, amount: String = "") {
        val comment = LiveComment(
            user = _currentUser.value,
            message = text,
            isDonation = isDonation,
            donationAmount = amount
        )
        _liveComments.update { current ->
            val list = current[streamId] ?: emptyList()
            current + (streamId to (list + comment))
        }
    }

    fun sendLiveHeart(streamId: String) {
        _liveStreams.update { list ->
            list.map { stream ->
                if (stream.id == streamId) {
                    stream.copy(likesCount = stream.likesCount + 1)
                } else stream
            }
        }
    }

    fun goLive(title: String, category: String): LiveStream {
        val stream = LiveStream(
            id = "live_" + UUID.randomUUID().toString().take(6),
            host = _currentUser.value,
            title = title,
            category = category,
            viewerCount = 1,
            likesCount = 0,
            isLive = true,
            bannerUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1000&auto=format&fit=crop&q=80"
        )
        _liveStreams.update { listOf(stream) + it }
        return stream
    }

    fun updateProfile(displayName: String, bio: String, website: String, avatarUrl: String, bannerUrl: String) {
        _currentUser.update {
            it.copy(
                displayName = displayName,
                bio = bio,
                website = website,
                avatarUrl = avatarUrl.ifBlank { it.avatarUrl },
                bannerUrl = bannerUrl.ifBlank { it.bannerUrl }
            )
        }
    }

    // Admin & Moderation methods
    fun toggleUserBan(userId: String) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    user.copy(isBanned = !user.isBanned)
                } else user
            }
        }
    }

    fun toggleUserVerification(userId: String, status: VerificationStatus) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    val newStatus = if (user.verification == status) VerificationStatus.NONE else status
                    user.copy(verification = newStatus)
                } else user
            }
        }
    }

    fun resolveReport(reportId: String, resolution: String) {
        _reports.update { list ->
            list.map { report ->
                if (report.id == reportId) {
                    report.copy(status = resolution)
                } else report
            }
        }
    }

    fun reportContent(reporterName: String, reportedItemId: String, itemType: String, snippet: String, reason: String) {
        val newReport = ContentReport(
            reporterId = _currentUser.value.id,
            reporterName = reporterName,
            reportedItemId = reportedItemId,
            itemType = itemType,
            reportedContentSnippet = snippet,
            reason = reason,
            status = "Pending"
        )
        _reports.update { listOf(newReport) + it }
    }

    fun toggleBlockUser(userId: String) {
        _blockedUserIds.update { current ->
            if (current.contains(userId)) current - userId else current + userId
        }
    }

    fun toggleAdCampaign(campaignId: String) {
        _adCampaigns.update { list ->
            list.map { ad ->
                if (ad.id == campaignId) {
                    ad.copy(isActive = !ad.isActive)
                } else ad
            }
        }
    }

    fun login(emailOrPhone: String, method: String) {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
