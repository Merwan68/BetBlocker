package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SyncMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncMetadataDao {

    @Query("SELECT * FROM sync_metadata WHERE `key` = :key LIMIT 1")
    suspend fun getMetadata(key: String): SyncMetadataEntity?

    @Query("SELECT * FROM sync_metadata WHERE `key` = :key LIMIT 1")
    fun getMetadataFlow(key: String): Flow<SyncMetadataEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMetadata(metadata: SyncMetadataEntity)

    @Query("DELETE FROM sync_metadata WHERE `key` = :key")
    suspend fun deleteMetadata(key: String)
}
