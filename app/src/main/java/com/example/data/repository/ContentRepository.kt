package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.core.database.AppDatabase
import com.example.core.database.VlogCacheEntity
import com.example.data.datasource.HardikContentDataSource
import com.example.domain.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ContentRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var firestore: FirebaseFirestore? = null

    private val _vlogs = MutableStateFlow<List<Vlog>>(HardikContentDataSource.initialVlogs)
    val vlogs: StateFlow<List<Vlog>> = _vlogs.asStateFlow()

    private val _shorts = MutableStateFlow<List<ShortVideo>>(HardikContentDataSource.initialShorts)
    val shorts: StateFlow<List<ShortVideo>> = _shorts.asStateFlow()

    private val _photos = MutableStateFlow<List<GalleryPhoto>>(HardikContentDataSource.initialPhotos)
    val photos: StateFlow<List<GalleryPhoto>> = _photos.asStateFlow()

    private val _categories = MutableStateFlow<List<ContentCategory>>(HardikContentDataSource.categories)
    val categories: StateFlow<List<ContentCategory>> = _categories.asStateFlow()

    private val _banners = MutableStateFlow<List<PromoBanner>>(emptyList())
    val banners: StateFlow<List<PromoBanner>> = _banners.asStateFlow()

    private val _socialLinks = MutableStateFlow<List<SocialPlatformLink>>(HardikContentDataSource.socialLinks)
    val socialLinks: StateFlow<List<SocialPlatformLink>> = _socialLinks.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(HardikContentDataSource.initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _appSettings = MutableStateFlow(GlobalAppSettings())
    val appSettings: StateFlow<GlobalAppSettings> = _appSettings.asStateFlow()

    private val _likedVlogIds = MutableStateFlow<Set<String>>(emptySet())
    val likedVlogIds: StateFlow<Set<String>> = _likedVlogIds.asStateFlow()

    private val _likedShortIds = MutableStateFlow<Set<String>>(emptySet())
    val likedShortIds: StateFlow<Set<String>> = _likedShortIds.asStateFlow()

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                Log.i("ContentRepository", "Firebase Firestore connected successfully")
            } else {
                Log.w("ContentRepository", "FirebaseApp not ready during ContentRepository init")
            }
        } catch (e: Exception) {
            Log.e("ContentRepository", "Firestore init exception: ${e.message}", e)
        }
        initializeCacheAndCloud()
    }

    private fun initializeCacheAndCloud() {
        scope.launch {
            // Save initial seed into Room database for offline cache
            try {
                val entities = HardikContentDataSource.initialVlogs.map { v ->
                    VlogCacheEntity(
                        id = v.id,
                        title = v.title,
                        description = v.description,
                        thumbnailUrl = v.thumbnailUrl,
                        videoUrl = v.videoUrl,
                        youtubeId = v.youtubeId,
                        categoryId = v.categoryId,
                        categoryName = v.categoryName,
                        publishedAt = v.publishedAt,
                        timestamp = v.timestamp,
                        duration = v.duration,
                        durationSeconds = v.durationSeconds,
                        views = v.views,
                        likes = v.likes,
                        isFeatured = v.isFeatured,
                        status = v.status
                    )
                }
                database.vlogCacheDao().insertVlogs(entities)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Failed to populate local cache", e)
            }

            // Sync with Firestore
            syncWithFirestore()
        }
    }

    private suspend fun syncWithFirestore() {
        val db = firestore ?: return
        try {
            // 1. Sync Vlogs
            val vlogsSnapshot = db.collection("vlogs").get().await()
            if (!vlogsSnapshot.isEmpty) {
                val remoteVlogs = vlogsSnapshot.documents.mapNotNull { doc ->
                    try {
                        val isPublished = doc.getBoolean("published") ?: (doc.getString("status") != "draft")
                        Vlog(
                            id = doc.getString("id") ?: doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                            videoUrl = doc.getString("videoUrl") ?: "",
                            youtubeId = doc.getString("youtubeId"),
                            categoryId = doc.getString("categoryId") ?: "cat_travel",
                            categoryName = doc.getString("categoryName") ?: "Travel",
                            publishedAt = doc.getString("publishedAt") ?: "Recently",
                            timestamp = doc.getLong("createdAt") ?: doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            duration = doc.getString("duration") ?: "15:00",
                            durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 0,
                            views = doc.getLong("views") ?: 0L,
                            likes = doc.getLong("likes") ?: 0L,
                            isFeatured = doc.getBoolean("featured") ?: doc.getBoolean("isFeatured") ?: false,
                            status = if (isPublished) "published" else "draft"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remoteVlogs.isNotEmpty()) {
                    _vlogs.value = remoteVlogs
                }
            } else {
                // Seed initial vlogs into Firestore so backend is genuinely populated
                seedInitialFirestoreVlogs(db)
            }

            // 2. Sync Shorts
            val shortsSnapshot = db.collection("shorts").get().await()
            if (!shortsSnapshot.isEmpty) {
                val remoteShorts = shortsSnapshot.documents.mapNotNull { doc ->
                    try {
                        val isPub = doc.getBoolean("published") ?: (doc.getString("status") != "draft")
                        ShortVideo(
                            id = doc.getString("id") ?: doc.id,
                            title = doc.getString("title") ?: "",
                            videoUrl = doc.getString("videoUrl") ?: "",
                            thumbnailUrl = doc.getString("thumbnailUrl") ?: "",
                            publishedAt = doc.getString("publishedAt") ?: "Recently",
                            views = doc.getLong("views") ?: 0L,
                            likes = doc.getLong("likes") ?: 0L,
                            creatorName = doc.getString("creatorName") ?: "Hardik",
                            creatorHandle = doc.getString("creatorHandle") ?: "@hardiklifevlog",
                            soundTitle = doc.getString("soundTitle") ?: "Original Audio • Hardik Life Vlog",
                            status = if (isPub) "published" else "draft"
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remoteShorts.isNotEmpty()) {
                    _shorts.value = remoteShorts
                }
            } else {
                seedInitialFirestoreShorts(db)
            }

            // 3. Sync Gallery Photos
            val photosSnapshot = db.collection("gallery").get().await()
            if (!photosSnapshot.isEmpty) {
                val remotePhotos = photosSnapshot.documents.mapNotNull { doc ->
                    try {
                        GalleryPhoto(
                            id = doc.getString("id") ?: doc.id,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            caption = doc.getString("caption") ?: "",
                            categoryId = doc.getString("categoryId") ?: "cat_travel",
                            categoryName = doc.getString("categoryName") ?: "Travel",
                            publishedAt = doc.getString("publishedAt") ?: "Recently",
                            location = doc.getString("location") ?: "",
                            likes = doc.getLong("likes") ?: 0L,
                            aspectRatio = doc.getDouble("aspectRatio")?.toFloat() ?: 1.0f
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remotePhotos.isNotEmpty()) {
                    _photos.value = remotePhotos
                }
            } else {
                seedInitialFirestorePhotos(db)
            }

            // 4. Sync Categories
            val categoriesSnapshot = db.collection("categories").get().await()
            if (!categoriesSnapshot.isEmpty) {
                val remoteCategories = categoriesSnapshot.documents.mapNotNull { doc ->
                    try {
                        ContentCategory(
                            id = doc.getString("id") ?: doc.id,
                            name = doc.getString("name") ?: "",
                            iconName = doc.getString("iconName") ?: "",
                            count = doc.getLong("count")?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remoteCategories.isNotEmpty()) {
                    _categories.value = remoteCategories
                }
            } else {
                seedInitialFirestoreCategories(db)
            }

            // 5. Sync Banners
            val bannersSnapshot = db.collection("banners").get().await()
            if (!bannersSnapshot.isEmpty) {
                val remoteBanners = bannersSnapshot.documents.mapNotNull { doc ->
                    try {
                        PromoBanner(
                            id = doc.getString("id") ?: doc.id,
                            title = doc.getString("title") ?: "",
                            subtitle = doc.getString("subtitle") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            targetType = doc.getString("targetType") ?: "vlog",
                            targetId = doc.getString("targetId") ?: "",
                            isActive = doc.getBoolean("isActive") ?: true,
                            priority = doc.getLong("priority")?.toInt() ?: 1
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _banners.value = remoteBanners
            }

            // 6. Sync Notifications
            val notifsSnapshot = db.collection("notifications").get().await()
            if (!notifsSnapshot.isEmpty) {
                val remoteNotifs = notifsSnapshot.documents.mapNotNull { doc ->
                    try {
                        AppNotification(
                            id = doc.getString("id") ?: doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            targetType = doc.getString("targetType") ?: "vlog",
                            targetId = doc.getString("targetId") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                            isRead = doc.getBoolean("isRead") ?: false
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                if (remoteNotifs.isNotEmpty()) {
                    _notifications.value = remoteNotifs
                }
            }

            // 7. Sync App Settings
            val settingsDoc = db.collection("settings").document("general").get().await()
            if (settingsDoc.exists()) {
                _appSettings.value = GlobalAppSettings(
                    maintenanceMode = settingsDoc.getBoolean("maintenanceMode") ?: false,
                    maintenanceMessage = settingsDoc.getString("maintenanceMessage") ?: _appSettings.value.maintenanceMessage,
                    minAppVersion = settingsDoc.getString("minAppVersion") ?: "1.0.0",
                    forceUpdate = settingsDoc.getBoolean("forceUpdate") ?: false,
                    supportEmail = settingsDoc.getString("supportEmail") ?: _appSettings.value.supportEmail,
                    termsUrl = settingsDoc.getString("termsUrl") ?: _appSettings.value.termsUrl,
                    privacyUrl = settingsDoc.getString("privacyUrl") ?: _appSettings.value.privacyUrl,
                    creatorName = settingsDoc.getString("creatorName") ?: _appSettings.value.creatorName,
                    creatorBio = settingsDoc.getString("creatorBio") ?: _appSettings.value.creatorBio,
                    creatorSubscribers = settingsDoc.getString("creatorSubscribers") ?: _appSettings.value.creatorSubscribers
                )
            }

        } catch (e: Exception) {
            Log.d("ContentRepository", "Firestore sync note: ${e.message}")
        }
    }

    private suspend fun seedInitialFirestoreVlogs(db: FirebaseFirestore) {
        try {
            HardikContentDataSource.initialVlogs.forEach { vlog ->
                val data = mapOf(
                    "id" to vlog.id,
                    "title" to vlog.title,
                    "description" to vlog.description,
                    "thumbnailUrl" to vlog.thumbnailUrl,
                    "videoUrl" to vlog.videoUrl,
                    "youtubeId" to (vlog.youtubeId ?: ""),
                    "categoryId" to vlog.categoryId,
                    "categoryName" to vlog.categoryName,
                    "published" to (vlog.status == "published"),
                    "featured" to vlog.isFeatured,
                    "createdAt" to vlog.timestamp,
                    "updatedAt" to vlog.timestamp,
                    "duration" to vlog.duration,
                    "durationSeconds" to vlog.durationSeconds,
                    "views" to vlog.views,
                    "likes" to vlog.likes,
                    "status" to vlog.status
                )
                db.collection("vlogs").document(vlog.id).set(data).await()
            }
        } catch (e: Exception) {
            Log.w("ContentRepository", "Initial vlog seed note: ${e.message}")
        }
    }

    private suspend fun seedInitialFirestoreShorts(db: FirebaseFirestore) {
        try {
            HardikContentDataSource.initialShorts.forEach { short ->
                val data = mapOf(
                    "id" to short.id,
                    "title" to short.title,
                    "videoUrl" to short.videoUrl,
                    "thumbnailUrl" to short.thumbnailUrl,
                    "published" to (short.status == "published"),
                    "views" to short.views,
                    "likes" to short.likes,
                    "createdAt" to System.currentTimeMillis(),
                    "creatorName" to short.creatorName,
                    "creatorHandle" to short.creatorHandle,
                    "soundTitle" to short.soundTitle,
                    "status" to short.status
                )
                db.collection("shorts").document(short.id).set(data).await()
            }
        } catch (e: Exception) {
            Log.w("ContentRepository", "Initial shorts seed note: ${e.message}")
        }
    }

    private suspend fun seedInitialFirestorePhotos(db: FirebaseFirestore) {
        try {
            HardikContentDataSource.initialPhotos.forEach { photo ->
                val data = mapOf(
                    "id" to photo.id,
                    "imageUrl" to photo.imageUrl,
                    "caption" to photo.caption,
                    "categoryId" to photo.categoryId,
                    "categoryName" to photo.categoryName,
                    "location" to photo.location,
                    "likes" to photo.likes,
                    "aspectRatio" to photo.aspectRatio,
                    "published" to true,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("gallery").document(photo.id).set(data).await()
            }
        } catch (e: Exception) {
            Log.w("ContentRepository", "Initial photos seed note: ${e.message}")
        }
    }

    private suspend fun seedInitialFirestoreCategories(db: FirebaseFirestore) {
        try {
            HardikContentDataSource.categories.forEach { cat ->
                val data = mapOf(
                    "id" to cat.id,
                    "name" to cat.name,
                    "iconName" to cat.iconName,
                    "count" to cat.count
                )
                db.collection("categories").document(cat.id).set(data).await()
            }
        } catch (e: Exception) {
            Log.w("ContentRepository", "Initial categories seed note: ${e.message}")
        }
    }

    // Engagement methods
    fun toggleLikeVlog(vlogId: String) {
        val current = _likedVlogIds.value.toMutableSet()
        val isNowLiked = if (current.contains(vlogId)) {
            current.remove(vlogId)
            false
        } else {
            current.add(vlogId)
            true
        }
        _likedVlogIds.value = current

        _vlogs.value = _vlogs.value.map { vlog ->
            if (vlog.id == vlogId) {
                val newLikes = if (isNowLiked) vlog.likes + 1 else maxOf(0, vlog.likes - 1)
                scope.launch {
                    try {
                        firestore?.collection("vlogs")?.document(vlogId)?.update("likes", newLikes)
                    } catch (e: Exception) {
                        Log.d("ContentRepository", "Like sync note: ${e.message}")
                    }
                }
                vlog.copy(likes = newLikes, isLiked = isNowLiked)
            } else vlog
        }
    }

    fun toggleLikeShort(shortId: String) {
        val current = _likedShortIds.value.toMutableSet()
        val isNowLiked = if (current.contains(shortId)) {
            current.remove(shortId)
            false
        } else {
            current.add(shortId)
            true
        }
        _likedShortIds.value = current

        _shorts.value = _shorts.value.map { short ->
            if (short.id == shortId) {
                val newLikes = if (isNowLiked) short.likes + 1 else maxOf(0, short.likes - 1)
                scope.launch {
                    try {
                        firestore?.collection("shorts")?.document(shortId)?.update("likes", newLikes)
                    } catch (e: Exception) {
                        Log.d("ContentRepository", "Like short note: ${e.message}")
                    }
                }
                short.copy(likes = newLikes, isLiked = isNowLiked)
            } else short
        }
    }

    fun incrementVlogViews(vlogId: String) {
        _vlogs.value = _vlogs.value.map { vlog ->
            if (vlog.id == vlogId) {
                val newViews = vlog.views + 1
                scope.launch {
                    try {
                        firestore?.collection("vlogs")?.document(vlogId)?.update("views", newViews)
                    } catch (e: Exception) {
                        Log.d("ContentRepository", "View count note: ${e.message}")
                    }
                }
                vlog.copy(views = newViews)
            } else vlog
        }
    }

    fun getVlogById(id: String): Vlog? {
        return _vlogs.value.find { it.id == id }
    }

    fun getRelatedVlogs(currentVlogId: String, categoryId: String): List<Vlog> {
        val sameCategory = _vlogs.value.filter { it.id != currentVlogId && it.categoryId == categoryId && it.status == "published" }
        return if (sameCategory.isNotEmpty()) {
            sameCategory
        } else {
            _vlogs.value.filter { it.id != currentVlogId && it.status == "published" }.take(4)
        }
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // ==========================================
    // REAL ADMIN OPERATIONS (FIRESTORE + REALTIME)
    // ==========================================

    // Vlogs Management
    suspend fun addVlog(vlog: Vlog): Result<Unit> = withContext(Dispatchers.IO) {
        _vlogs.value = listOf(vlog) + _vlogs.value.filter { it.id != vlog.id }
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "id" to vlog.id,
                    "title" to vlog.title,
                    "description" to vlog.description,
                    "thumbnailUrl" to vlog.thumbnailUrl,
                    "videoUrl" to vlog.videoUrl,
                    "youtubeId" to (vlog.youtubeId ?: ""),
                    "categoryId" to vlog.categoryId,
                    "categoryName" to vlog.categoryName,
                    "published" to (vlog.status == "published"),
                    "featured" to vlog.isFeatured,
                    "createdAt" to vlog.timestamp,
                    "updatedAt" to System.currentTimeMillis(),
                    "duration" to vlog.duration,
                    "durationSeconds" to vlog.durationSeconds,
                    "views" to vlog.views,
                    "likes" to vlog.likes,
                    "status" to vlog.status
                )
                db.collection("vlogs").document(vlog.id).set(data).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error adding vlog to Firestore", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun updateVlog(vlog: Vlog): Result<Unit> = withContext(Dispatchers.IO) {
        _vlogs.value = _vlogs.value.map { if (it.id == vlog.id) vlog else it }
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "title" to vlog.title,
                    "description" to vlog.description,
                    "thumbnailUrl" to vlog.thumbnailUrl,
                    "videoUrl" to vlog.videoUrl,
                    "categoryId" to vlog.categoryId,
                    "categoryName" to vlog.categoryName,
                    "published" to (vlog.status == "published"),
                    "featured" to vlog.isFeatured,
                    "updatedAt" to System.currentTimeMillis(),
                    "duration" to vlog.duration,
                    "status" to vlog.status
                )
                db.collection("vlogs").document(vlog.id).set(data, SetOptions.merge()).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error updating vlog in Firestore", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun deleteVlog(vlogId: String): Result<Unit> = withContext(Dispatchers.IO) {
        _vlogs.value = _vlogs.value.filter { it.id != vlogId }
        val db = firestore
        if (db != null) {
            try {
                db.collection("vlogs").document(vlogId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error deleting vlog from Firestore", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun togglePublishVlog(vlogId: String, publish: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        val newStatus = if (publish) "published" else "draft"
        _vlogs.value = _vlogs.value.map {
            if (it.id == vlogId) it.copy(status = newStatus) else it
        }
        val db = firestore
        if (db != null) {
            try {
                val updates = mapOf(
                    "published" to publish,
                    "status" to newStatus,
                    "updatedAt" to System.currentTimeMillis()
                )
                db.collection("vlogs").document(vlogId).update(updates).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error toggling vlog publish status", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun toggleFeaturedVlog(vlogId: String, isFeatured: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        _vlogs.value = _vlogs.value.map {
            if (it.id == vlogId) it.copy(isFeatured = isFeatured)
            else if (isFeatured) it.copy(isFeatured = false) // only 1 primary featured
            else it
        }
        val db = firestore
        if (db != null) {
            try {
                db.collection("vlogs").document(vlogId).update("featured", isFeatured).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error toggling featured vlog", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    // Shorts Management
    suspend fun addShort(short: ShortVideo): Result<Unit> = withContext(Dispatchers.IO) {
        _shorts.value = listOf(short) + _shorts.value.filter { it.id != short.id }
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "id" to short.id,
                    "title" to short.title,
                    "videoUrl" to short.videoUrl,
                    "thumbnailUrl" to short.thumbnailUrl,
                    "published" to (short.status == "published"),
                    "views" to short.views,
                    "likes" to short.likes,
                    "createdAt" to System.currentTimeMillis(),
                    "creatorName" to short.creatorName,
                    "creatorHandle" to short.creatorHandle,
                    "soundTitle" to short.soundTitle,
                    "status" to short.status
                )
                db.collection("shorts").document(short.id).set(data).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error adding short to Firestore", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun deleteShort(shortId: String): Result<Unit> = withContext(Dispatchers.IO) {
        _shorts.value = _shorts.value.filter { it.id != shortId }
        val db = firestore
        if (db != null) {
            try {
                db.collection("shorts").document(shortId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error deleting short", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun togglePublishShort(shortId: String, publish: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        val newStatus = if (publish) "published" else "draft"
        _shorts.value = _shorts.value.map {
            if (it.id == shortId) it.copy(status = newStatus) else it
        }
        val db = firestore
        if (db != null) {
            try {
                db.collection("shorts").document(shortId).update(
                    mapOf("published" to publish, "status" to newStatus)
                ).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    // Gallery Photos Management
    suspend fun addPhoto(photo: GalleryPhoto): Result<Unit> = withContext(Dispatchers.IO) {
        _photos.value = listOf(photo) + _photos.value.filter { it.id != photo.id }
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "id" to photo.id,
                    "imageUrl" to photo.imageUrl,
                    "caption" to photo.caption,
                    "categoryId" to photo.categoryId,
                    "categoryName" to photo.categoryName,
                    "location" to photo.location,
                    "likes" to photo.likes,
                    "aspectRatio" to photo.aspectRatio,
                    "published" to true,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("gallery").document(photo.id).set(data).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error adding photo", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun deletePhoto(photoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        _photos.value = _photos.value.filter { it.id != photoId }
        val db = firestore
        if (db != null) {
            try {
                db.collection("gallery").document(photoId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    // Category Management
    suspend fun addCategory(category: ContentCategory): Result<Unit> = withContext(Dispatchers.IO) {
        _categories.value = _categories.value + category
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "id" to category.id,
                    "name" to category.name,
                    "iconName" to category.iconName,
                    "count" to category.count
                )
                db.collection("categories").document(category.id).set(data).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> = withContext(Dispatchers.IO) {
        _categories.value = _categories.value.filter { it.id != categoryId }
        val db = firestore
        if (db != null) {
            try {
                db.collection("categories").document(categoryId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    // Banner Management
    suspend fun addBanner(banner: PromoBanner): Result<Unit> = withContext(Dispatchers.IO) {
        _banners.value = listOf(banner) + _banners.value.filter { it.id != banner.id }
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "id" to banner.id,
                    "title" to banner.title,
                    "subtitle" to banner.subtitle,
                    "imageUrl" to banner.imageUrl,
                    "targetType" to banner.targetType,
                    "targetId" to banner.targetId,
                    "isActive" to banner.isActive,
                    "priority" to banner.priority
                )
                db.collection("banners").document(banner.id).set(data).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun toggleBannerActive(bannerId: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        _banners.value = _banners.value.map {
            if (it.id == bannerId) it.copy(isActive = isActive) else it
        }
        val db = firestore
        if (db != null) {
            try {
                db.collection("banners").document(bannerId).update("isActive", isActive).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun deleteBanner(bannerId: String): Result<Unit> = withContext(Dispatchers.IO) {
        _banners.value = _banners.value.filter { it.id != bannerId }
        val db = firestore
        if (db != null) {
            try {
                db.collection("banners").document(bannerId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    // Notification Management
    suspend fun broadcastNotification(
        title: String,
        message: String,
        targetType: String = "vlog",
        targetId: String = "",
        sentBy: String = "admin"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val newNotif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            targetType = targetType,
            targetId = targetId,
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        _notifications.value = listOf(newNotif) + _notifications.value

        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "id" to newNotif.id,
                    "title" to newNotif.title,
                    "message" to newNotif.message,
                    "targetType" to newNotif.targetType,
                    "targetId" to newNotif.targetId,
                    "timestamp" to newNotif.timestamp,
                    "createdAt" to System.currentTimeMillis(),
                    "sentBy" to sentBy,
                    "isRead" to false
                )
                db.collection("notifications").document(newNotif.id).set(data).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error sending notification to Firestore", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun deleteNotification(notificationId: String): Result<Unit> = withContext(Dispatchers.IO) {
        _notifications.value = _notifications.value.filter { it.id != notificationId }
        val db = firestore
        if (db != null) {
            try {
                db.collection("notifications").document(notificationId).delete().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    // App Settings & Creator Profile Management
    suspend fun updateAppSettings(settings: GlobalAppSettings): Result<Unit> = withContext(Dispatchers.IO) {
        _appSettings.value = settings
        val db = firestore
        if (db != null) {
            try {
                val data = mapOf(
                    "maintenanceMode" to settings.maintenanceMode,
                    "maintenanceMessage" to settings.maintenanceMessage,
                    "minAppVersion" to settings.minAppVersion,
                    "forceUpdate" to settings.forceUpdate,
                    "supportEmail" to settings.supportEmail,
                    "termsUrl" to settings.termsUrl,
                    "privacyUrl" to settings.privacyUrl,
                    "creatorName" to settings.creatorName,
                    "creatorBio" to settings.creatorBio,
                    "creatorSubscribers" to settings.creatorSubscribers
                )
                db.collection("settings").document("general").set(data, SetOptions.merge()).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("ContentRepository", "Error updating settings in Firestore", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    suspend fun updateSocialLinks(links: List<SocialPlatformLink>): Result<Unit> = withContext(Dispatchers.IO) {
        _socialLinks.value = links
        val db = firestore
        if (db != null) {
            try {
                links.forEach { link ->
                    val data = mapOf(
                        "id" to link.id,
                        "platform" to link.platform,
                        "title" to link.title,
                        "url" to link.url,
                        "iconType" to link.iconType
                    )
                    db.collection("social_links").document(link.id).set(data).await()
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ContentRepository? = null

        fun getInstance(context: Context, database: AppDatabase): ContentRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ContentRepository(context.applicationContext, database)
                INSTANCE = instance
                instance
            }
        }
    }
}
