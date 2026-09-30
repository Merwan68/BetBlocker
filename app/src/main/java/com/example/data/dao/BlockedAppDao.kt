package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Query("SELECT * FROM blocked_apps WHERE isDeleted = 0 ORDER BY appName ASC")
    fun getAllAppsFlow(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE isActive = 1 AND isDeleted = 0")
    fun getActiveAppsFlow(): Flow<List<BlockedAppEntity>>

    @Query("SELECT packageName FROM blocked_apps WHERE isActive = 1 AND isDeleted = 0")
    suspend fun getActivePackageNameList(): List<String>

    @Query("SELECT * FROM blocked_apps WHERE isCustom = 1 AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getCustomAppsFlow(): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE packageName = :packageName AND isDeleted = 0 LIMIT 1")
    suspend fun getApp(packageName: String): BlockedAppEntity?

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE packageName = :packageName AND isActive = 1 AND isDeleted = 0")
    suspend fun isPackageBlocked(packageName: String): Int

    @Query("SELECT * FROM blocked_apps WHERE isDeleted = 0 AND (packageName LIKE '%' || :query || '%' OR appName LIKE '%' || :query || '%')")
    fun searchAppsFlow(query: String): Flow<List<BlockedAppEntity>>

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE isDeleted = 0")
    fun getTotalCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE isActive = 1 AND isDeleted = 0")
    fun getActiveCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE isCustom = 1 AND isDeleted = 0")
    fun getCustomCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: BlockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<BlockedAppEntity>)

    @Update
    suspend fun updateApp(app: BlockedAppEntity)

    @Query("UPDATE blocked_apps SET isActive = :isActive, updatedAt = :updatedAt WHERE packageName = :packageName")
    suspend fun setAppActive(packageName: String, isActive: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE blocked_apps SET isDeleted = 1, updatedAt = :updatedAt WHERE packageName = :packageName")
    suspend fun markDeleted(packageName: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deletePermanently(packageName: String)

    @Query("SELECT * FROM blocked_apps WHERE updatedAt > :sinceTimestamp")
    suspend fun getModifiedSince(sinceTimestamp: Long): List<BlockedAppEntity>

    @Query("SELECT MAX(syncVersion) FROM blocked_apps")
    suspend fun getHighestSyncVersion(): Long?
}
