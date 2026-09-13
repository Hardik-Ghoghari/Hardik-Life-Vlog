package com.example.domain.model

data class Vlog(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val youtubeId: String? = null,
    val categoryId: String,
    val categoryName: String,
    val publishedAt: String,
    val timestamp: Long = System.currentTimeMillis(),
    val duration: String,
    val durationSeconds: Int = 0,
    val views: Long = 0,
    val likes: Long = 0,
    val isFeatured: Boolean = false,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val status: String = "published"
)

data class ShortVideo(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val views: Long = 0,
    val likes: Long = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val creatorName: String = "Hardik",
    val creatorHandle: String = "@hardiklifevlog",
    val soundTitle: String = "Original Audio • Hardik Life Vlog",
    val status: String = "published"
)

data class GalleryPhoto(
    val id: String,
    val imageUrl: String,
    val caption: String,
    val categoryId: String,
    val categoryName: String,
    val publishedAt: String,
    val location: String = "",
    val likes: Long = 0,
    val aspectRatio: Float = 1.0f
)

data class ContentCategory(
    val id: String,
    val name: String,
    val iconName: String = "",
    val count: Int = 0
)

data class UserProfile(
    val userId: String,
    val name: String,
    val email: String,
    val profileImage: String = "",
    val photoUrl: String = "",
    val bio: String = "",
    val role: String = "user", // "admin" or "user"
    val status: String = "active", // "active" or "suspended"
    val isGuest: Boolean = false,
    val isAdmin: Boolean = false,
    val totalVlogsWatched: Int = 0,
    val totalBookmarks: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserAccount(
    val uid: String,
    val name: String,
    val email: String,
    val photoUrl: String = "",
    val role: String = "user", // "admin" or "user"
    val status: String = "active", // "active" or "suspended"
    val createdAt: Long = System.currentTimeMillis()
)

data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val imageUrl: String,
    val targetType: String = "vlog",
    val targetId: String = "",
    val isActive: Boolean = true,
    val priority: Int = 1
)

data class GlobalAppSettings(
    val maintenanceMode: Boolean = false,
    val maintenanceMessage: String = "Hardik Life Vlog is undergoing maintenance. We'll be back shortly!",
    val minAppVersion: String = "1.0.0",
    val forceUpdate: Boolean = false,
    val supportEmail: String = "support@hardiklifevlog.com",
    val termsUrl: String = "https://hardiklifevlog.com/terms",
    val privacyUrl: String = "https://hardiklifevlog.com/privacy",
    val creatorName: String = "Hardik",
    val creatorBio: String = "Exploring India & the world! New vlogs every week capturing daily adventures, food, and culture.",
    val creatorSubscribers: String = "450K+"
)

data class SocialPlatformLink(
    val id: String,
    val platform: String,
    val title: String,
    val url: String,
    val iconType: String // "youtube", "instagram", "facebook", "telegram", "whatsapp", "x"
)

data class WatchHistoryItem(
    val id: String,
    val contentId: String,
    val contentType: String = "vlog", // "vlog" or "short"
    val title: String,
    val thumbnailUrl: String,
    val duration: String,
    val progressSeconds: Long,
    val totalDurationSeconds: Long,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

data class BookmarkItem(
    val id: String,
    val contentId: String,
    val contentType: String = "vlog",
    val title: String,
    val thumbnailUrl: String,
    val categoryName: String,
    val duration: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val targetType: String = "vlog", // "vlog", "short", "gallery", "creator_update"
    val targetId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class VlogSortOrder {
    NEWEST,
    POPULAR
}

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

enum class AppLanguage {
    ENGLISH,
    GUJARATI,
    HINDI
}
