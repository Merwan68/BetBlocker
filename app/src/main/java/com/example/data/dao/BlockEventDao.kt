package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.BlockEventEntity
import com.example.data.model.CategoryCount
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockEventDao {

    @Insert
    suspend fun insertEvent(event: BlockEventEntity): Long

    @Query("SELECT * FROM block_events ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEventsFlow(limit: Int = 50): Flow<List<BlockEventEntity>>

    @Query("SELECT COUNT(*) FROM block_events")
    fun getTotalBlockedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM block_events WHERE timestamp >= :sinceTimestamp")
    fun getBlockedCountSinceFlow(sinceTimestamp: Long): Flow<Int>

    @Query("SELECT category, COUNT(*) as count FROM block_events GROUP BY category ORDER BY count DESC")
    fun getCategoryCountsFlow(): Flow<List<CategoryCount>>

    @Query("SELECT COUNT(*) FROM block_events WHERE timestamp >= :sinceTimestamp")
    suspend fun getBlockedCountSince(sinceTimestamp: Long): Int

    @Query("DELETE FROM block_events")
    suspend fun clearAllEvents()
}
