package com.example.data.repository

import com.example.data.dao.BlockEventDao
import com.example.data.dao.BlockedAppDao
import com.example.data.dao.BlockedDomainDao
import com.example.data.dao.SyncMetadataDao
import com.example.data.model.BlockEventEntity
import com.example.data.model.BlockedAppEntity
import com.example.data.model.BlockedDomainEntity
import com.example.data.model.CategoryCount
import com.example.data.model.SyncMetadataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

class BlockingRepository(
    private val domainDao: BlockedDomainDao,
    private val appDao: BlockedAppDao,
    private val eventDao: BlockEventDao,
    private val syncMetadataDao: SyncMetadataDao
) {

    // Domain flows
    val allDomainsFlow: Flow<List<BlockedDomainEntity>> = domainDao.getAllDomainsFlow()
    val activeDomainsFlow: Flow<List<BlockedDomainEntity>> = domainDao.getActiveDomainsFlow()
    val customDomainsFlow: Flow<List<BlockedDomainEntity>> = domainDao.getCustomDomainsFlow()
    val totalDomainCountFlow: Flow<Int> = domainDao.getTotalCountFlow()
    val activeDomainCountFlow: Flow<Int> = domainDao.getActiveCountFlow()

    // App flows
    val allAppsFlow: Flow<List<BlockedAppEntity>> = appDao.getAllAppsFlow()
    val activeAppsFlow: Flow<List<BlockedAppEntity>> = appDao.getActiveAppsFlow()
    val customAppsFlow: Flow<List<BlockedAppEntity>> = appDao.getCustomAppsFlow()
    val totalAppCountFlow: Flow<Int> = appDao.getTotalCountFlow()
    val activeAppCountFlow: Flow<Int> = appDao.getActiveCountFlow()

    // Event & Statistics flows
    val recentEventsFlow: Flow<List<BlockEventEntity>> = eventDao.getRecentEventsFlow(50)
    val totalBlockedCountFlow: Flow<Int> = eventDao.getTotalBlockedCountFlow()
    val categoryCountsFlow: Flow<List<CategoryCount>> = eventDao.getCategoryCountsFlow()

    fun getBlockedTodayFlow(): Flow<Int> {
        val startOfDay = getStartOfDayTimestamp()
        return eventDao.getBlockedCountSinceFlow(startOfDay)
    }

    fun getBlockedThisWeekFlow(): Flow<Int> {
        val startOfWeek = getStartOfWeekTimestamp()
        return eventDao.getBlockedCountSinceFlow(startOfWeek)
    }

    // Fast check for VPN DNS interceptor
    suspend fun isDomainBlocked(host: String): Boolean = withContext(Dispatchers.IO) {
        val cleanHost = host.trim().lowercase().removePrefix("www.")
        if (cleanHost.isBlank()) return@withContext false

        // Check exact match
        if (domainDao.isDomainBlocked(cleanHost) > 0) return@withContext true

        // Check root domain if subdomain query (e.g. sports.bet365.com -> bet365.com)
        val parts = cleanHost.split(".")
        if (parts.size > 2) {
            val rootDomain = parts.takeLast(2).joinToString(".")
            if (domainDao.isDomainBlocked(rootDomain) > 0) return@withContext true
        }
        false
    }

    // Fast check for Accessibility Service app interceptor
    suspend fun isPackageBlocked(packageName: String): Boolean = withContext(Dispatchers.IO) {
        if (packageName.isBlank()) return@withContext false
        appDao.isPackageBlocked(packageName) > 0
    }

    suspend fun getAppDetails(packageName: String): BlockedAppEntity? = withContext(Dispatchers.IO) {
        appDao.getApp(packageName)
    }

    suspend fun getDomainDetails(domain: String): BlockedDomainEntity? = withContext(Dispatchers.IO) {
        domainDao.getDomain(domain)
    }

    // Custom user-added rules
    suspend fun addCustomDomain(domain: String, serviceName: String, category: String): Boolean = withContext(Dispatchers.IO) {
        val cleanDomain = domain.trim().lowercase().removePrefix("https://").removePrefix("http://").removePrefix("www.")
        if (cleanDomain.isBlank()) return@withContext false

        val entity = BlockedDomainEntity(
            domain = cleanDomain,
            serviceName = serviceName.ifBlank { cleanDomain },
            category = category,
            country = "CUSTOM",
            isActive = true,
            isCustom = true,
            isDeleted = false,
            updatedAt = System.currentTimeMillis()
        )
        domainDao.insertDomain(entity)
        true
    }

    suspend fun addCustomApp(packageName: String, appName: String, category: String): Boolean = withContext(Dispatchers.IO) {
        if (packageName.isBlank()) return@withContext false
        val entity = BlockedAppEntity(
            packageName = packageName.trim(),
            appName = appName.ifBlank { packageName },
            company = "User Custom",
            category = category,
            country = "CUSTOM",
            isActive = true,
            isCustom = true,
            isDeleted = false,
            updatedAt = System.currentTimeMillis()
        )
        appDao.insertApp(entity)
        true
    }

    suspend fun toggleDomainStatus(domain: String, isActive: Boolean) = withContext(Dispatchers.IO) {
        domainDao.setDomainActive(domain, isActive)
    }

    suspend fun toggleAppStatus(packageName: String, isActive: Boolean) = withContext(Dispatchers.IO) {
        appDao.setAppActive(packageName, isActive)
    }

    suspend fun removeCustomDomain(domain: String) = withContext(Dispatchers.IO) {
        val existing = domainDao.getDomain(domain)
        if (existing?.isCustom == true) {
            domainDao.deletePermanently(domain)
        } else {
            domainDao.setDomainActive(domain, false)
        }
    }

    suspend fun removeCustomApp(packageName: String) = withContext(Dispatchers.IO) {
        val existing = appDao.getApp(packageName)
        if (existing?.isCustom == true) {
            appDao.deletePermanently(packageName)
        } else {
            appDao.setAppActive(packageName, false)
        }
    }

    // Log block attempt
    suspend fun recordBlockEvent(
        target: String,
        serviceName: String,
        category: String,
        targetType: String
    ): Long = withContext(Dispatchers.IO) {
        val event = BlockEventEntity(
            target = target,
            serviceName = serviceName,
            category = category,
            targetType = targetType,
            timestamp = System.currentTimeMillis()
        )
        eventDao.insertEvent(event)
    }

    // Remote sync support
    suspend fun syncRemoteCatalog(
        remoteDomains: List<BlockedDomainEntity>,
        remoteApps: List<BlockedAppEntity>,
        remoteVersion: Long
    ) = withContext(Dispatchers.IO) {
        // Upsert non-custom remote domains and apps
        domainDao.insertDomains(remoteDomains.map { it.copy(isCustom = false) })
        appDao.insertApps(remoteApps.map { it.copy(isCustom = false) })

        syncMetadataDao.upsertMetadata(
            SyncMetadataEntity(
                key = "catalog_version",
                value = remoteVersion.toString(),
                updatedAt = System.currentTimeMillis()
            )
        )
        syncMetadataDao.upsertMetadata(
            SyncMetadataEntity(
                key = "last_synced_at",
                value = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getCatalogVersion(): Long = withContext(Dispatchers.IO) {
        val meta = syncMetadataDao.getMetadata("catalog_version")
        meta?.value?.toLongOrNull() ?: 1L
    }

    suspend fun getLastSyncedTimestamp(): Long = withContext(Dispatchers.IO) {
        val meta = syncMetadataDao.getMetadata("last_synced_at")
        meta?.value?.toLongOrNull() ?: 0L
    }

    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getStartOfWeekTimestamp(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
