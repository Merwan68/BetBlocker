package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BlockedDomainEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedDomainDao {

    @Query("SELECT * FROM blocked_domains WHERE isDeleted = 0 ORDER BY domain ASC")
    fun getAllDomainsFlow(): Flow<List<BlockedDomainEntity>>

    @Query("SELECT * FROM blocked_domains WHERE isActive = 1 AND isDeleted = 0")
    fun getActiveDomainsFlow(): Flow<List<BlockedDomainEntity>>

    @Query("SELECT domain FROM blocked_domains WHERE isActive = 1 AND isDeleted = 0")
    suspend fun getActiveDomainList(): List<String>

    @Query("SELECT * FROM blocked_domains WHERE isCustom = 1 AND isDeleted = 0 ORDER BY updatedAt DESC")
    fun getCustomDomainsFlow(): Flow<List<BlockedDomainEntity>>

    @Query("SELECT * FROM blocked_domains WHERE domain = :domain AND isDeleted = 0 LIMIT 1")
    suspend fun getDomain(domain: String): BlockedDomainEntity?

    @Query("SELECT COUNT(*) FROM blocked_domains WHERE domain = :domain AND isActive = 1 AND isDeleted = 0")
    suspend fun isDomainBlocked(domain: String): Int

    @Query("SELECT * FROM blocked_domains WHERE isDeleted = 0 AND (domain LIKE '%' || :query || '%' OR serviceName LIKE '%' || :query || '%')")
    fun searchDomainsFlow(query: String): Flow<List<BlockedDomainEntity>>

    @Query("SELECT COUNT(*) FROM blocked_domains WHERE isDeleted = 0")
    fun getTotalCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_domains WHERE isActive = 1 AND isDeleted = 0")
    fun getActiveCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM blocked_domains WHERE isCustom = 1 AND isDeleted = 0")
    fun getCustomCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomain(domain: BlockedDomainEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomains(domains: List<BlockedDomainEntity>)

    @Update
    suspend fun updateDomain(domain: BlockedDomainEntity)

    @Query("UPDATE blocked_domains SET isActive = :isActive, updatedAt = :updatedAt WHERE domain = :domain")
    suspend fun setDomainActive(domain: String, isActive: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE blocked_domains SET isDeleted = 1, updatedAt = :updatedAt WHERE domain = :domain")
    suspend fun markDeleted(domain: String, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM blocked_domains WHERE domain = :domain")
    suspend fun deletePermanently(domain: String)

    @Query("SELECT * FROM blocked_domains WHERE updatedAt > :sinceTimestamp")
    suspend fun getModifiedSince(sinceTimestamp: Long): List<BlockedDomainEntity>

    @Query("SELECT MAX(syncVersion) FROM blocked_domains")
    suspend fun getHighestSyncVersion(): Long?
}
