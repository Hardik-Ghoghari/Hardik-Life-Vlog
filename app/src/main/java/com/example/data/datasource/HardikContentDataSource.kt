package com.example.data.datasource

import com.example.domain.model.*

object HardikContentDataSource {

    val categories = listOf(
        ContentCategory(id = "cat_all", name = "All", count = 16),
        ContentCategory(id = "cat_travel", name = "Travel", count = 5),
        ContentCategory(id = "cat_daily", name = "Daily Life", count = 4),
        ContentCategory(id = "cat_food", name = "Food", count = 3),
        ContentCategory(id = "cat_adventure", name = "Adventure", count = 3),
        ContentCategory(id = "cat_lifestyle", name = "Lifestyle", count = 2),
        ContentCategory(id = "cat_family", name = "Family", count = 2),
        ContentCategory(id = "cat_friends", name = "Friends", count = 2),
        ContentCategory(id = "cat_events", name = "Events", count = 2)
    )

    val galleryCategories = listOf(
        "All",
        "Travel",
        "Daily Life",
        "Events",
        "Behind the Scenes",
        "Memories"
    )

    val socialLinks = listOf(
        SocialPlatformLink(
            id = "soc_yt",
            platform = "YouTube",
            title = "Hardik Life Vlog",
            url = "https://www.youtube.com/@hardiklifevlog",
            iconType = "youtube"
        ),
        SocialPlatformLink(
            id = "soc_ig",
            platform = "Instagram",
            title = "@hardik.lifevlog",
            url = "https://www.instagram.com/hardiklifevlog",
            iconType = "instagram"
        ),
        SocialPlatformLink(
            id = "soc_fb",
            platform = "Facebook",
            title = "Hardik Life Vlog Official",
            url = "https://www.facebook.com/hardiklifevlog",
            iconType = "facebook"
        ),
        SocialPlatformLink(
            id = "soc_tg",
            platform = "Telegram",
            title = "HLV Community Channel",
            url = "https://t.me/hardiklifevlog",
            iconType = "telegram"
        ),
        SocialPlatformLink(
            id = "soc_wa",
            platform = "WhatsApp Channel",
            title = "Hardik Broadcast",
            url = "https://whatsapp.com/channel/hardiklifevlog",
            iconType = "whatsapp"
        ),
        SocialPlatformLink(
            id = "soc_x",
            platform = "X (Twitter)",
            title = "@hardikvlog",
            url = "https://x.com/hardiklifevlog",
            iconType = "x"
        )
    )

    // Working video sample URLs (reliable Google/Android developer public media streams for smooth playback)
    private const val VIDEO_SAMPLE_1 = "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    private const val VIDEO_SAMPLE_2 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    private const val VIDEO_SAMPLE_3 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    private const val VIDEO_SAMPLE_4 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    private const val VIDEO_SAMPLE_5 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
    private const val VIDEO_SAMPLE_6 = "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4"

    val initialVlogs = listOf(
        Vlog(
            id = "vlog_1",
            title = "48 Hours in Tokyo: Hidden Food Alleys & Midnight Supercars",
            description = "Welcome back to another adventure! In this episode, we spend an unforgettable 48 hours exploring Tokyo from underground ramen shops to neon-drenched midnight highway drives through Shibuya. Thank you for all your support on the channel!",
            thumbnailUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1200&q=80",
            videoUrl = VIDEO_SAMPLE_1,
            youtubeId = "tokyo_vlog_48h",
            categoryId = "cat_travel",
            categoryName = "Travel",
            publishedAt = "2 days ago",
            timestamp = System.currentTimeMillis() - 172800000L,
            duration = "24:18",
            durationSeconds = 1458,
            views = 184500,
            likes = 14200,
            isFeatured = true
        ),
        Vlog(
            id = "vlog_2",
            title = "My Real Daily Morning Routine as a Full-Time Creator",
            description = "Ever wondered what a typical day looks like behind the scenes? From 6 AM workouts and shooting b-roll to coffee, editing timelines, and script writing. Here is an honest, unfiltered look at my daily life.",
            thumbnailUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=1200&q=80",
            videoUrl = VIDEO_SAMPLE_2,
            youtubeId = "daily_routine_hardik",
            categoryId = "cat_daily",
            categoryName = "Daily Life",
            publishedAt = "4 days ago",
            timestamp = System.currentTimeMillis() - 345600000L,
            duration = "16:42",
            durationSeconds = 1002,
            views = 98200,
            likes = 8900,
            isFeatured = false
        ),
        Vlog(
            id = "vlog_3",
            title = "Riding Through Ladakh: The Most Dangerous Mountain Pass!",
            description = "We took our motorcycles across the Khardung La pass at 17,500 feet! Oxygen was low, winds were brutal, but the landscapes were out of this world. Make sure to watch till the end for the sunrise over Pangong Lake.",
            thumbnailUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=1200&q=80",
            videoUrl = VIDEO_SAMPLE_3,
            youtubeId = "ladakh_moto_pass",
            categoryId = "cat_adventure",
            categoryName = "Adventure",
            publishedAt = "1 week ago",
            timestamp = System.currentTimeMillis() - 604800000L,
            duration = "31:05",
            durationSeconds = 1865,
            views = 312000,
            likes = 27400,
            isFeatured = false
        ),
        Vlog(
            id = "vlog_4",
            title = "Gujarat Midnight Food Tour: 10 Iconic Street Eats in Ahmedabad!",
            description = "Ahmedabad at 1 AM is the ultimate paradise for food lovers! We tried butter maska buns, jalebi fafda, manek chowk sandwiches, and refreshing ice crushers. Let me know your favorite Gujarati dish in the comments!",
            thumbnailUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=1200&q=80",
            videoUrl = VIDEO_SAMPLE_4,
            youtubeId = "ahmedabad_food_tour",
            categoryId = "cat_food",
            categoryName = "Food",
            publishedAt = "2 weeks ago",
            timestamp = System.currentTimeMillis() - 1209600000L,
            duration = "19:50",
            durationSeconds = 1190,
            views = 425000,
            likes = 39800,
            isFeatured = false
        ),
        Vlog(
            id = "vlog_5",
            title = "Surprising Mom & Dad With Their Dream Family Vacation!",
            description = "This was one of the most emotional days of my life. For years my parents dreamed of visiting Switzerland, and thanks to you all, I was finally able to surprise them with first-class flight tickets!",
            thumbnailUrl = "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=1200&q=80",
            videoUrl = VIDEO_SAMPLE_5,
            youtubeId = "family_surprise_trip",
            categoryId = "cat_family",
            categoryName = "Family",
            publishedAt = "3 weeks ago",
            timestamp = System.currentTimeMillis() - 1814400000L,
            duration = "22:15",
            durationSeconds = 1335,
            views = 560000,
            likes = 54200,
            isFeatured = false
        ),
        Vlog(
            id = "vlog_6",
            title = "Building My Dream Cinema Studio & Gear Tour 2026",
            description = "A comprehensive breakdown of all the cameras, lenses, microphones, acoustic treatments, and custom lighting setups we use to produce our 4K cinematic vlogs and shorts.",
            thumbnailUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=1200&q=80",
            videoUrl = VIDEO_SAMPLE_6,
            youtubeId = "studio_gear_tour",
            categoryId = "cat_lifestyle",
            categoryName = "Lifestyle",
            publishedAt = "1 month ago",
            timestamp = System.currentTimeMillis() - 2592000000L,
            duration = "18:30",
            durationSeconds = 1110,
            views = 142000,
            likes = 11300,
            isFeatured = false
        )
    )

    val initialShorts = listOf(
        ShortVideo(
            id = "short_1",
            title = "When you drop your camera lens in a mountain stream 😱 #fails #vloglife",
            videoUrl = VIDEO_SAMPLE_1,
            thumbnailUrl = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&q=80",
            publishedAt = "Yesterday",
            views = 485000,
            likes = 36200
        ),
        ShortVideo(
            id = "short_2",
            title = "Craziest 50 Rupee Street Food find! Pure deliciousness 🤤",
            videoUrl = VIDEO_SAMPLE_2,
            thumbnailUrl = "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=800&q=80",
            publishedAt = "3 days ago",
            views = 890000,
            likes = 72400
        ),
        ShortVideo(
            id = "short_3",
            title = "15-second sunrise over the snow peaks of Ladakh 🏔️✨",
            videoUrl = VIDEO_SAMPLE_3,
            thumbnailUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&q=80",
            publishedAt = "5 days ago",
            views = 640000,
            likes = 58900
        ),
        ShortVideo(
            id = "short_4",
            title = "Pack my everyday vlog backpack with me in 30 seconds! 🎒",
            videoUrl = VIDEO_SAMPLE_4,
            thumbnailUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
            publishedAt = "1 week ago",
            views = 310000,
            likes = 29500
        ),
        ShortVideo(
            id = "short_5",
            title = "How we capture those silky smooth drone tracking shots 🚁🎬",
            videoUrl = VIDEO_SAMPLE_5,
            thumbnailUrl = "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=800&q=80",
            publishedAt = "2 weeks ago",
            views = 720000,
            likes = 64100
        )
    )

    val initialPhotos = listOf(
        GalleryPhoto(
            id = "photo_1",
            imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1200&q=80",
            caption = "Midnight reflections in Shibuya. Quiet moments between the rush.",
            categoryId = "Travel",
            categoryName = "Travel",
            publishedAt = "Sept 10, 2026",
            location = "Tokyo, Japan",
            likes = 4200,
            aspectRatio = 1.33f
        ),
        GalleryPhoto(
            id = "photo_2",
            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1200&q=80",
            caption = "First light over the high-altitude desert. Chilly 2°C morning.",
            categoryId = "Travel",
            categoryName = "Travel",
            publishedAt = "Sept 6, 2026",
            location = "Pangong Tso, Ladakh",
            likes = 6100,
            aspectRatio = 1.5f
        ),
        GalleryPhoto(
            id = "photo_3",
            imageUrl = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=1200&q=80",
            caption = "Workspace late night edit session. Color grading the Tokyo film.",
            categoryId = "Behind the Scenes",
            categoryName = "Behind the Scenes",
            publishedAt = "Sept 2, 2026",
            location = "HLV Studio",
            likes = 3400,
            aspectRatio = 1.0f
        ),
        GalleryPhoto(
            id = "photo_4",
            imageUrl = "https://images.unsplash.com/photo-1511895426328-dc8714191300?w=1200&q=80",
            caption = "Family dinner laughter. Pure happiness captured on film.",
            categoryId = "Memories",
            categoryName = "Memories",
            publishedAt = "Aug 28, 2026",
            location = "Ahmedabad, Gujarat",
            likes = 8900,
            aspectRatio = 1.25f
        ),
        GalleryPhoto(
            id = "photo_5",
            imageUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=1200&q=80",
            caption = "Standing on top of the world. Khardung La Pass milestone.",
            categoryId = "Events",
            categoryName = "Events",
            publishedAt = "Aug 20, 2026",
            location = "Ladakh, India",
            likes = 5700,
            aspectRatio = 1.33f
        ),
        GalleryPhoto(
            id = "photo_6",
            imageUrl = "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=1200&q=80",
            caption = "Freshly made crispy street jalebi. Sizzle and aroma in the air.",
            categoryId = "Daily Life",
            categoryName = "Daily Life",
            publishedAt = "Aug 15, 2026",
            location = "Manek Chowk, Ahmedabad",
            likes = 4800,
            aspectRatio = 1.1f
        )
    )

    val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "🔥 New Vlog is Live!",
            message = "48 Hours in Tokyo: Hidden Food Alleys & Midnight Supercars has just uploaded.",
            targetType = "vlog",
            targetId = "vlog_1",
            timestamp = System.currentTimeMillis() - 3600000L,
            isRead = false
        ),
        AppNotification(
            id = "notif_2",
            title = "📸 New Behind The Scenes Photos",
            message = "Check out exclusive high-res photo frames from our Japan trip in the Gallery.",
            targetType = "gallery",
            targetId = "photo_1",
            timestamp = System.currentTimeMillis() - 86400000L,
            isRead = false
        ),
        AppNotification(
            id = "notif_3",
            title = "⚡ New Short: Mountain Camera Disaster!",
            message = "What happened when we dropped the lens in the river? Watch now.",
            targetType = "short",
            targetId = "short_1",
            timestamp = System.currentTimeMillis() - 172800000L,
            isRead = true
        )
    )
}
