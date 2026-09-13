package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val contentId: String,
    val contentType: String = "vlog",
    val title: String,
    val thumbnailUrl: String,
    val categoryName: String,
    val duration: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val contentId: String,
    val contentType: String = "vlog",
    val title: String,
    val thumbnailUrl: String,
    val duration: String,
    val progressSeconds: Long,
    val totalDurationSeconds: Long,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_vlogs")
data class VlogCacheEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val youtubeId: String?,
    val categoryId: String,
    val categoryName: String,
    val publishedAt: String,
    val timestamp: Long,
    val duration: String,
    val durationSeconds: Int,
    val views: Long,
    val likes: Long,
    val isFeatured: Boolean,
    val status: String
)
