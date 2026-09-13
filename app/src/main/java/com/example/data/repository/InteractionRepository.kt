package com.example.data.repository

import com.example.core.database.AppDatabase
import com.example.core.database.BookmarkEntity
import com.example.core.database.WatchHistoryEntity
import com.example.domain.model.BookmarkItem
import com.example.domain.model.Vlog
import com.example.domain.model.WatchHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InteractionRepository(
    private val database: AppDatabase
) {
    val bookmarks: Flow<List<BookmarkItem>> =
        database.bookmarkDao().getAllBookmarks().map { list ->
            list.map { entity ->
                BookmarkItem(
                    id = entity.contentId,
                    contentId = entity.contentId,
                    contentType = entity.contentType,
                    title = entity.title,
                    thumbnailUrl = entity.thumbnailUrl,
                    categoryName = entity.categoryName,
                    duration = entity.duration,
                    savedTimestamp = entity.savedTimestamp
                )
            }
        }

    val watchHistory: Flow<List<WatchHistoryItem>> =
        database.watchHistoryDao().getWatchHistory().map { list ->
            list.map { entity ->
                WatchHistoryItem(
                    id = entity.contentId,
                    contentId = entity.contentId,
                    contentType = entity.contentType,
                    title = entity.title,
                    thumbnailUrl = entity.thumbnailUrl,
                    duration = entity.duration,
                    progressSeconds = entity.progressSeconds,
                    totalDurationSeconds = entity.totalDurationSeconds,
                    lastWatchedTimestamp = entity.lastWatchedTimestamp
                )
            }
        }

    fun isVlogBookmarked(contentId: String): Flow<Boolean> {
        return database.bookmarkDao().isBookmarked(contentId)
    }

    suspend fun toggleBookmark(vlog: Vlog): Boolean {
        val isBookmarked = database.bookmarkDao().isBookmarkedSync(vlog.id)
        if (isBookmarked) {
            database.bookmarkDao().deleteBookmark(vlog.id)
            return false
        } else {
            database.bookmarkDao().insertBookmark(
                BookmarkEntity(
                    contentId = vlog.id,
                    contentType = "vlog",
                    title = vlog.title,
                    thumbnailUrl = vlog.thumbnailUrl,
                    categoryName = vlog.categoryName,
                    duration = vlog.duration,
                    savedTimestamp = System.currentTimeMillis()
                )
            )
            return true
        }
    }

    suspend fun removeBookmark(contentId: String) {
        database.bookmarkDao().deleteBookmark(contentId)
    }

    suspend fun clearAllBookmarks() {
        database.bookmarkDao().clearAllBookmarks()
    }

    suspend fun recordWatchProgress(
        vlog: Vlog,
        progressSeconds: Long,
        totalDurationSeconds: Long
    ) {
        database.watchHistoryDao().insertOrUpdateHistory(
            WatchHistoryEntity(
                contentId = vlog.id,
                contentType = "vlog",
                title = vlog.title,
                thumbnailUrl = vlog.thumbnailUrl,
                duration = vlog.duration,
                progressSeconds = progressSeconds,
                totalDurationSeconds = totalDurationSeconds,
                lastWatchedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearWatchHistory() {
        database.watchHistoryDao().clearHistory()
    }

    companion object {
        @Volatile
        private var INSTANCE: InteractionRepository? = null

        fun getInstance(database: AppDatabase): InteractionRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = InteractionRepository(database)
                INSTANCE = instance
                instance
            }
        }
    }
}
