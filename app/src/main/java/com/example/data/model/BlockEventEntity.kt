package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Privacy-conscious log of a blocked attempt.
 * Never stores URLs with query params, personal messages, or user credentials.
 */
@Entity(
    tableName = "block_events",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["category"]),
        Index(value = ["targetType"])
    ]
)
data class BlockEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val target: String,
    val serviceName: String,
    val category: String,
    val targetType: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class CategoryCount(
    val category: String,
    val count: Int
)
