package com.example.data.model

import java.util.UUID

enum class PostType {
    IMAGE,
    VIDEO,
    CAROUSEL,
    ADVERTISEMENT
}

enum class StoryType {
    IMAGE,
    VIDEO,
    TEXT
}

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE,
    POST_SHARE
}

enum class VerificationStatus {
    NONE,
    VERIFIED_CREATOR,
    VERIFIED_OFFICIAL,
    VERIFIED_ADMIN
}

enum class UserRole {
    USER,
    CREATOR,
    MODERATOR,
    ADMIN
}

enum class ReportStatus {
    PENDING,
    RESOLVED,
    DISMISSED
}

enum class ModerationAction {
    DELETE_POST,
    BAN_USER,
    DISMISS,
    WARN_USER
}

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    MENTION,
    LIVE
}

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val avatarUrl: String,
    val bannerUrl: String = "",
    val bio: String = "",
    val website: String = "",
    val verification: VerificationStatus = VerificationStatus.NONE,
    val role: UserRole = UserRole.USER,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val postsCount: Int = 0,
    val isFollowing: Boolean = false,
    val isBanned: Boolean = false,
    val isOnline: Boolean = true,
    val isCloseFriend: Boolean = false,
    val isBlocked: Boolean = false,
    val joinedDate: String = "August 2026"
)

data class Story(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val user: User,
    val mediaUrl: String,
    val textOverlay: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSeen: Boolean = false,
    val likesCount: Int = 0,
    val isLiked: Boolean = false
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val postId: String,
    val user: User,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val repliesCount: Int = 0
)

data class Post(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val user: User,
    val type: PostType = PostType.IMAGE,
    val mediaUrls: List<String>,
    val caption: String,
    val tags: List<String> = emptyList(),
    val location: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isSponsored: Boolean = false,
    val sponsorCtaText: String? = null,
    val sponsorCtaUrl: String? = null
)

data class Reel(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val user: User,
    val videoUrl: String,
    val thumbnailUrl: String,
    val caption: String,
    val audioTrackName: String = "Original Audio - " + user.displayName,
    val audioAuthor: String = user.displayName,
    val tags: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val type: MessageType = MessageType.TEXT,
    val content: String = "",
    val mediaUrl: String? = null,
    val voiceDurationSeconds: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val isEncrypted: Boolean = true,
    val reactions: List<String> = emptyList()
) {
    val isVoiceNote: Boolean get() = type == MessageType.VOICE || voiceDurationSeconds > 0
    val audioDurationSeconds: Int get() = voiceDurationSeconds
    val formattedTime: String get() = "Just now"
}

data class ChatRoom(
    val id: String = UUID.randomUUID().toString(),
    val isGroup: Boolean = false,
    val title: String,
    val avatarUrl: String,
    val participants: List<User> = emptyList(),
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val isEncrypted: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val displayName: String get() = title
    val displayAvatar: String get() = avatarUrl
    val lastMessageText: String get() = lastMessage?.content ?: "Start chatting..."
    val lastMessageTime: String get() = "Just now"
    val participantIds: List<String> get() = participants.map { it.id }
}

typealias ChatThread = ChatRoom

data class LiveStream(
    val id: String = UUID.randomUUID().toString(),
    val host: User,
    val title: String,
    val category: String = "Creative & Lifestyle",
    val viewerCount: Int = 0,
    val likesCount: Int = 0,
    val isLive: Boolean = true,
    val bannerUrl: String = "",
    val startedAt: Long = System.currentTimeMillis()
) {
    val viewersCount: Int get() = viewerCount
    val previewThumbnail: String get() = bannerUrl.ifBlank { "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&auto=format&fit=crop" }
}

data class LiveComment(
    val id: String = UUID.randomUUID().toString(),
    val user: User,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDonation: Boolean = false,
    val donationAmount: String = ""
) {
    val content: String get() = message
}

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val user: User,
    val actionType: String = "liked",
    val targetPreview: String? = null,
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetPostId: String? = null
) {
    val actorUser: User get() = user
    val content: String get() = message.ifBlank { "$actionType your post" }
    val targetMediaUrl: String? get() = targetPreview
    val formattedTime: String get() = "2m ago"
    val type: NotificationType get() = when (actionType) {
        "commented" -> NotificationType.COMMENT
        "followed" -> NotificationType.FOLLOW
        "mentioned" -> NotificationType.MENTION
        "live" -> NotificationType.LIVE
        else -> NotificationType.LIKE
    }
}

data class ContentReport(
    val id: String = UUID.randomUUID().toString(),
    val reporterId: String = "",
    val reporterName: String = "",
    val reportedItemId: String = "",
    val itemType: String = "post",
    val reportedContentSnippet: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending"
) {
    val targetId: String get() = reportedItemId
    val targetType: String get() = itemType
    val formattedTime: String get() = "5m ago"
    val reporterUser: User get() = User(id = reporterId, username = reporterName.lowercase().replace(" ", "_"), displayName = reporterName, avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100")
    val statusEnum: ReportStatus get() = when (status) {
        "Resolved", "Resolved - Post Deleted", "Resolved - User Banned", "Resolved - Warning Sent" -> ReportStatus.RESOLVED
        "Dismissed" -> ReportStatus.DISMISSED
        else -> ReportStatus.PENDING
    }
}

data class AdCampaign(
    val id: String = UUID.randomUUID().toString(),
    val brandName: String,
    val brandLogoUrl: String,
    val headline: String,
    val mediaUrl: String,
    val ctaText: String = "Learn More",
    val impressions: Int = 0,
    val clicks: Int = 0,
    val budget: String = "$500.00",
    val isActive: Boolean = true
) {
    val title: String get() = "$brandName • $headline"
    val budgetUSD: String get() = budget.replace("$", "")
    val targetAudience: String get() = "Global 18-35 Creators"
}
