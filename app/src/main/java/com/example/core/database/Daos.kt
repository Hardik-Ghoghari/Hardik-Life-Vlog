package com.example.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY savedTimestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE contentId = :contentId)")
    fun isBookmarked(contentId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE contentId = :contentId)")
    suspend fun isBookmarkedSync(contentId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE contentId = :contentId")
    suspend fun deleteBookmark(contentId: String)

    @Query("DELETE FROM bookmarks")
    suspend fun clearAllBookmarks()
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE contentId = :contentId LIMIT 1")
    suspend fun getHistoryItem(contentId: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHistory(item: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE contentId = :contentId")
    suspend fun deleteHistoryItem(contentId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearHistory()
}

@Dao
interface VlogCacheDao {
    @Query("SELECT * FROM cached_vlogs ORDER BY timestamp DESC")
    fun getAllCachedVlogs(): Flow<List<VlogCacheEntity>>

    @Query("SELECT * FROM cached_vlogs WHERE isFeatured = 1 LIMIT 1")
    fun getFeaturedVlog(): Flow<VlogCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVlogs(vlogs: List<VlogCacheEntity>)

    @Query("DELETE FROM cached_vlogs")
    suspend fun clearVlogCache()
}
