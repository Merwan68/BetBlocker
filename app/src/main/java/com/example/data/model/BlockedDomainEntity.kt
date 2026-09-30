package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a gambling or betting domain to be blocked at the DNS/network level.
 * Supports remote sync metadata (syncVersion, isDeleted, updatedAt).
 */
@Entity(
    tableName = "blocked_domains",
    indices = [
        Index(value = ["domain"], unique = true),
        Index(value = ["category"]),
        Index(value = ["isActive"]),
        Index(value = ["updatedAt"])
    ]
)
data class BlockedDomainEntity(
    @PrimaryKey
    val domain: String,
    val serviceName: String,
    val category: String,
    val country: String = "GLOBAL",
    val isActive: Boolean = true,
    val isCustom: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncVersion: Long = 1L
)
