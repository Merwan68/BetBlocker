package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.data.AppDatabase
import com.example.data.repository.BlockingRepository
import com.example.security.PinManager
import com.example.security.ProtectionLevel
import com.example.ui.AccessBlockedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AppBlockerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var repository: BlockingRepository
    private lateinit var pinManager: PinManager

    private var lastBlockedPackage: String? = null
    private var lastBlockedTimestamp: Long = 0

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = BlockingRepository(
            db.blockedDomainDao(),
            db.blockedAppDao(),
            db.blockEventDao(),
            db.syncMetadataDao()
        )
        pinManager = PinManager(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkgName = event.packageName?.toString() ?: return
        if (pkgName.isBlank()) return

        // Ignore system and own app packages
        if (pkgName == packageName ||
            pkgName == "com.android.systemui" ||
            pkgName == "com.google.android.apps.nexuslauncher" ||
            pkgName == "com.android.launcher3" ||
            pkgName.startsWith("com.google.android.inputmethod")
        ) {
            return
        }

        // Check if overall protection is enabled and level supports app blocking
        if (!pinManager.isProtectionEnabled) return
        if (pinManager.protectionLevel == ProtectionLevel.BASIC) return

        // Throttle repeated triggers within 1 second for the same package
        val now = System.currentTimeMillis()
        if (pkgName == lastBlockedPackage && (now - lastBlockedTimestamp) < 1000) {
            return
        }

        serviceScope.launch {
            if (repository.isPackageBlocked(pkgName)) {
                lastBlockedPackage = pkgName
                lastBlockedTimestamp = now

                val appDetails = repository.getAppDetails(pkgName)
                val serviceName = appDetails?.appName ?: pkgName
                val category = appDetails?.category ?: "Gambling Application"

                // Log the block event in local database
                repository.recordBlockEvent(
                    target = pkgName,
                    serviceName = serviceName,
                    category = category,
                    targetType = "APP"
                )

                // Launch supportive Access Blocked screen
                val intent = Intent(this@AppBlockerAccessibilityService, AccessBlockedActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("EXTRA_TARGET", pkgName)
                    putExtra("EXTRA_SERVICE_NAME", serviceName)
                    putExtra("EXTRA_CATEGORY", category)
                }
                startActivity(intent)
            }
        }
    }

    override fun onInterrupt() {
        // Required callback
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
