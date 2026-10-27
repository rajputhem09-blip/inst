package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.LykoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FeedTab {
    FOR_YOU,
    FOLLOWING,
    EXPLORE
}

enum class NavigationDestination {
    FEED,
    EXPLORE,
    CREATE,
    REELS,
    PROFILE,
    MESSAGES,
    CHAT_DETAIL,
    STORY_VIEWER,
    LIVE_BROWSE,
    LIVE_ROOM,
    GO_LIVE,
    ADMIN_PANEL,
    NOTIFICATIONS,
    SETTINGS_PRIVACY,
    USER_PROFILE_DETAIL
}

class LykoViewModel(private val repository: LykoRepository = LykoRepository()) : ViewModel() {

    val currentUser = repository.currentUser
    val isLoggedIn = repository.isLoggedIn
    val allUsers = repository.allUsers
    val stories = repository.stories
    val feedPosts = repository.feedPosts
    val reels = repository.reels
    val chatRooms = repository.chatRooms
    val liveStreams = repository.liveStreams
    val notifications = repository.notifications
    val reports = repository.reports
    val adCampaigns = repository.adCampaigns
    val blockedUserIds = repository.blockedUserIds

    // Navigation & UI State
    private val _currentScreen = MutableStateFlow(NavigationDestination.FEED)
    val currentScreen = _currentScreen.asStateFlow()

    private val _selectedFeedTab = MutableStateFlow(FeedTab.FOR_YOU)
    val selectedFeedTab = _selectedFeedTab.asStateFlow()

    // Active story viewer
    private val _activeStoryIndex = MutableStateFlow(0)
    val activeStoryIndex = _activeStoryIndex.asStateFlow()

    // Active Chat
    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId = _selectedChatId.asStateFlow()
    val activeChatThreadId = selectedChatId
    val chatThreads = chatRooms

    // Active Live Stream
    private val _selectedLiveStreamId = MutableStateFlow<String?>(null)
    val selectedLiveStreamId = _selectedLiveStreamId.asStateFlow()
    val activeLiveStreamId = selectedLiveStreamId

    // Active Inspect User Profile
    private val _inspectedUserId = MutableStateFlow<String?>(null)
    val inspectedUserId = _inspectedUserId.asStateFlow()
    val selectedUserProfileId = inspectedUserId

    // Explore Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _exploreCategory = MutableStateFlow("Trending")
    val exploreCategory = _exploreCategory.asStateFlow()

    // Comments Sheet State
    private val _activeCommentPostId = MutableStateFlow<String?>(null)
    val activeCommentPostId = _activeCommentPostId.asStateFlow()

    // Share Sheet State
    private val _activeSharePost = MutableStateFlow<Post?>(null)
    val activeSharePost = _activeSharePost.asStateFlow()

    // Report Dialog State
    private val _reportTarget = MutableStateFlow<Pair<String, String>?>(null) // (itemId, itemType)
    val reportTarget = _reportTarget.asStateFlow()

    // Floating heart triggers for Live Stream
    private val _heartBurstCount = MutableStateFlow(0)
    val heartBurstCount = _heartBurstCount.asStateFlow()

    // Voice recording simulation
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice = _isRecordingVoice.asStateFlow()

    private val _voiceRecordingSeconds = MutableStateFlow(0)
    val voiceRecordingSeconds = _voiceRecordingSeconds.asStateFlow()

    // Toast / Snack message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage = _userMessage.asStateFlow()

    fun navigateTo(dest: NavigationDestination) {
        _currentScreen.value = dest
    }

    fun setFeedTab(tab: FeedTab) {
        _selectedFeedTab.value = tab
    }

    fun openStory(index: Int) {
        _activeStoryIndex.value = index
        _currentScreen.value = NavigationDestination.STORY_VIEWER
    }

    fun nextStory() {
        val count = stories.value.size
        if (_activeStoryIndex.value < count - 1) {
            _activeStoryIndex.value += 1
        } else {
            _currentScreen.value = NavigationDestination.FEED
        }
    }

    fun previousStory() {
        if (_activeStoryIndex.value > 0) {
            _activeStoryIndex.value -= 1
        }
    }

    fun openChat(chatId: String) {
        _selectedChatId.value = chatId
        _currentScreen.value = NavigationDestination.CHAT_DETAIL
    }

    fun openLiveStream(streamId: String) {
        _selectedLiveStreamId.value = streamId
        _currentScreen.value = NavigationDestination.LIVE_ROOM
    }

    fun openUserProfile(userId: String) {
        if (userId == currentUser.value.id) {
            _currentScreen.value = NavigationDestination.PROFILE
        } else {
            _inspectedUserId.value = userId
            _currentScreen.value = NavigationDestination.USER_PROFILE_DETAIL
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setExploreCategory(category: String) {
        _exploreCategory.value = category
    }

    fun openCommentsFor(postId: String) {
        _activeCommentPostId.value = postId
    }

    fun closeComments() {
        _activeCommentPostId.value = null
    }

    fun openShareSheet(post: Post) {
        _activeSharePost.value = post
    }

    fun closeShareSheet() {
        _activeSharePost.value = null
    }

    fun openReportDialog(itemId: String, itemType: String) {
        _reportTarget.value = Pair(itemId, itemType)
    }

    fun closeReportDialog() {
        _reportTarget.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
        viewModelScope.launch {
            delay(3000)
            if (_userMessage.value == msg) {
                _userMessage.value = null
            }
        }
    }

    fun toggleLikePost(postId: String) {
        repository.toggleLikePost(postId)
    }

    fun toggleSavePost(postId: String) {
        repository.toggleSavePost(postId)
        showMessage("Post saved to your collection")
    }

    fun toggleFollowUser(userId: String) {
        repository.toggleFollowUser(userId)
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return
        repository.addComment(postId, text)
    }

    fun getCommentsForPost(postId: String): StateFlow<List<Comment>> {
        return repository.postComments.map { it[postId] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun getMessagesForChat(chatId: String): StateFlow<List<Message>> {
        return repository.messages.map { it[chatId] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun getLiveComments(streamId: String): StateFlow<List<LiveComment>> {
        return repository.liveComments.map { it[streamId] ?: emptyList() }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun toggleLikeReel(reelId: String) {
        repository.toggleLikeReel(reelId)
    }

    fun sendChatMessage(chatId: String, text: String) {
        if (text.isBlank()) return
        repository.sendMessage(chatId, text, MessageType.TEXT)
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _voiceRecordingSeconds.value = 0
        viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                if (_isRecordingVoice.value) {
                    _voiceRecordingSeconds.value += 1
                }
            }
        }
    }

    fun finishVoiceRecording(chatId: String) {
        val duration = _voiceRecordingSeconds.value.coerceAtLeast(1)
        _isRecordingVoice.value = false
        repository.sendMessage(chatId, "Voice note (0:${if (duration < 10) "0$duration" else "$duration"})", MessageType.VOICE, duration)
        _voiceRecordingSeconds.value = 0
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _voiceRecordingSeconds.value = 0
    }

    fun createGroupChat(title: String, participantIds: List<String>) {
        if (title.isBlank() || participantIds.isEmpty()) {
            showMessage("Please enter a group title and select members")
            return
        }
        repository.createGroupChat(title, participantIds)
        showMessage("Group chat '$title' created!")
        _currentScreen.value = NavigationDestination.MESSAGES
    }

    fun sendLiveComment(streamId: String, text: String) {
        if (text.isBlank()) return
        repository.addLiveComment(streamId, text)
    }

    fun sendLiveTip(streamId: String, amount: String) {
        repository.addLiveComment(streamId, "Sent a $amount super-gift! 💎✨", isDonation = true, amount = amount)
        _heartBurstCount.value += 3
        showMessage("Sent $amount gift to host!")
    }

    fun sendLiveHeart(streamId: String) {
        repository.sendLiveHeart(streamId)
        _heartBurstCount.value += 1
    }

    fun goLive(title: String, category: String) {
        val stream = repository.goLive(title.ifBlank { "Live Studio Session" }, category)
        _selectedLiveStreamId.value = stream.id
        _currentScreen.value = NavigationDestination.LIVE_ROOM
        showMessage("You are now broadcasting live on Lyko!")
    }

    fun createStory(mediaUrl: String, textOverlay: String) {
        repository.createStory(mediaUrl, textOverlay)
        showMessage("Story added!")
        _currentScreen.value = NavigationDestination.FEED
    }

    // Admin studio functions
    fun toggleUserBan(userId: String) {
        repository.toggleUserBan(userId)
        val user = allUsers.value.find { it.id == userId }
        val status = if (user?.isBanned == true) "unbanned" else "banned"
        showMessage("User @${user?.username} has been $status.")
    }

    fun toggleUserVerification(userId: String, status: VerificationStatus) {
        repository.toggleUserVerification(userId, status)
        showMessage("User verification status updated.")
    }

    fun resolveReport(reportId: String, resolution: String) {
        repository.resolveReport(reportId, resolution)
        showMessage("Report status set to $resolution.")
    }

    fun submitReport(reason: String) {
        val target = _reportTarget.value ?: return
        repository.reportContent(
            reporterName = currentUser.value.displayName,
            reportedItemId = target.first,
            itemType = target.second,
            snippet = "User report for ${target.second} ID ${target.first}",
            reason = reason
        )
        _reportTarget.value = null
        showMessage("Report submitted to moderation team. Thank you.")
    }

    fun toggleBlockUser(userId: String) {
        repository.toggleBlockUser(userId)
        val isBlocked = blockedUserIds.value.contains(userId)
        showMessage(if (!isBlocked) "User blocked and restricted" else "User unblocked")
    }

    fun toggleAdCampaign(campaignId: String) {
        repository.toggleAdCampaign(campaignId)
    }

    fun openChatThread(chatId: String) {
        openChat(chatId)
    }

    fun getMessagesForThread(chatId: String): StateFlow<List<Message>> {
        return getMessagesForChat(chatId)
    }

    fun startOrGetDirectChatWith(targetUserId: String): String {
        val existing = chatRooms.value.find { !it.isGroup && it.participants.any { p -> p.id == targetUserId } }
        if (existing != null) return existing.id
        val targetUser = allUsers.value.find { it.id == targetUserId }
        val newId = "chat_" + targetUserId
        val newRoom = ChatRoom(
            id = newId,
            isGroup = false,
            title = targetUser?.displayName ?: "Direct Message",
            avatarUrl = targetUser?.avatarUrl ?: "",
            participants = listOf(currentUser.value, targetUser ?: User(id = targetUserId, username = "creator", displayName = "Creator", avatarUrl = "")),
            unreadCount = 0,
            isEncrypted = true
        )
        // Add to rooms
        return newId
    }

    fun sendMessage(threadId: String, content: String, mediaUrl: String? = null) {
        if (mediaUrl != null) {
            repository.sendMessage(threadId, content, MessageType.IMAGE)
        } else {
            sendChatMessage(threadId, content)
        }
    }

    fun sendVoiceNote(threadId: String, durationSeconds: Int) {
        repository.sendMessage(threadId, "Voice note (0:${if (durationSeconds < 10) "0$durationSeconds" else "$durationSeconds"})", MessageType.VOICE, durationSeconds)
    }

    fun getCommentsForLive(streamId: String): StateFlow<List<LiveComment>> {
        return getLiveComments(streamId)
    }

    fun addCommentToLive(streamId: String, text: String) {
        sendLiveComment(streamId, text)
    }

    fun startLiveStream(title: String, category: String): LiveStream {
        val stream = repository.goLive(title, category)
        _selectedLiveStreamId.value = stream.id
        return stream
    }

    fun setUserVerification(userId: String, status: VerificationStatus) {
        toggleUserVerification(userId, status)
    }

    fun markNotificationRead(notificationId: String) {
        // Mark read
    }

    fun updateProfile(displayName: String, bio: String, website: String, avatarUrl: String = "", bannerUrl: String = "") {
        repository.updateProfile(displayName, bio, website, avatarUrl, bannerUrl)
        showMessage("Profile updated successfully!")
    }

    fun createPost(
        caption: String,
        mediaUrls: List<String>,
        tags: List<String>,
        location: String,
        type: PostType = PostType.IMAGE,
        isSponsored: Boolean = false,
        sponsorCtaText: String? = null
    ) {
        repository.createPost(caption, mediaUrls, tags, location)
        showMessage("Your post is live on Lyko!")
        _currentScreen.value = NavigationDestination.FEED
    }

    fun resolveReport(reportId: String, action: ModerationAction) {
        val resolution = when (action) {
            ModerationAction.DELETE_POST -> "Resolved - Post Deleted"
            ModerationAction.BAN_USER -> "Resolved - User Banned"
            ModerationAction.DISMISS -> "Dismissed"
            ModerationAction.WARN_USER -> "Resolved - Warning Sent"
        }
        repository.resolveReport(reportId, resolution)
        showMessage("Report status set to $resolution.")
    }

    fun login(identifier: String, method: String) {
        repository.login(identifier, method)
        showMessage("Welcome back to Lyko!")
    }

    fun logout() {
        repository.logout()
        showMessage("Logged out successfully.")
    }
}
