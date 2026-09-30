package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an Android application package to be detected and blocked.
 * Supports remote sync metadata (syncVersion, isDeleted, updatedAt).
 */
@Entity(
    tableName = "blocked_apps",
    indices = [
        Index(value = ["packageName"], unique = true),
        Index(value = ["category"]),
        Index(value = ["isActive"]),
        Index(value = ["updatedAt"])
    ]
)
data class BlockedAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val company: String = "",
    val category: String,
    val country: String = "GLOBAL",
    val isActive: Boolean = true,
    val isCustom: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val syncVersion: Long = 1L
)
