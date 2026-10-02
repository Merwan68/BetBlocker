package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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

    private var lastBlockedTarget: String? = null
    private var lastBlockedTimestamp: Long = 0

    private val browserPackages = setOf(
        "com.android.chrome",
        "com.chrome.beta",
        "com.chrome.canary",
        "com.chrome.dev",
        "com.sec.android.app.sbrowser",
        "com.sec.android.app.sbrowser.beta",
        "org.mozilla.firefox",
        "org.mozilla.firefox_beta",
        "com.brave.browser",
        "com.opera.browser",
        "com.opera.mini.native",
        "com.microsoft.emmx",
        "com.duckduckgo.mobile.android",
        "com.ecosia.android",
        "com.vivaldi.browser"
    )

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
        val eventType = event.eventType
        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            eventType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) return

        val pkgName = event.packageName?.toString() ?: return
        if (pkgName.isBlank()) return

        // Ignore system and own app packages
        if (pkgName == packageName ||
            pkgName == "com.android.systemui" ||
            pkgName.startsWith("com.google.android.inputmethod")
        ) {
            return
        }

        if (!pinManager.isProtectionEnabled) return

        val now = System.currentTimeMillis()

        // 1. Check if the foreground app itself is a blocked gambling package
        if (pinManager.protectionLevel != ProtectionLevel.BASIC) {
            serviceScope.launch {
                if (repository.isPackageBlocked(pkgName)) {
                    if (pkgName == lastBlockedTarget && (now - lastBlockedTimestamp) < 1500) {
                        return@launch
                    }
                    lastBlockedTarget = pkgName
                    lastBlockedTimestamp = now

                    val appDetails = repository.getAppDetails(pkgName)
                    val serviceName = appDetails?.appName ?: pkgName
                    val category = appDetails?.category ?: "Gambling Application"

                    repository.recordBlockEvent(
                        target = pkgName,
                        serviceName = serviceName,
                        category = category,
                        targetType = "APP"
                    )

                    launchBlockingScreen(pkgName, serviceName, category)
                }
            }
        }

        // 2. Check if user is navigating to a gambling website in a web browser
        if (browserPackages.contains(pkgName)) {
            val rootNode = rootInActiveWindow ?: return
            serviceScope.launch {
                val detectedDomain = extractDomainFromBrowser(rootNode)
                if (!detectedDomain.isNullOrBlank() && detectedDomain.contains(".")) {
                    if (repository.isDomainBlocked(detectedDomain)) {
                        if (detectedDomain == lastBlockedTarget && (now - lastBlockedTimestamp) < 1500) {
                            return@launch
                        }
                        lastBlockedTarget = detectedDomain
                        lastBlockedTimestamp = now

                        val domainDetails = repository.getDomainDetails(detectedDomain)
                        val serviceName = domainDetails?.serviceName ?: detectedDomain
                        val category = domainDetails?.category ?: "Gambling Website"

                        repository.recordBlockEvent(
                            target = detectedDomain,
                            serviceName = serviceName,
                            category = category,
                            targetType = "DOMAIN"
                        )

                        launchBlockingScreen(detectedDomain, serviceName, category)
                    }
                }
            }
        }
    }

    private fun extractDomainFromBrowser(rootNode: AccessibilityNodeInfo): String? {
        val candidateIds = listOf(
            "com.android.chrome:id/url_bar",
            "com.chrome.beta:id/url_bar",
            "com.sec.android.app.sbrowser:id/location_bar_edit_text",
            "org.mozilla.firefox:id/url_bar_title",
            "org.mozilla.firefox:id/toolbar",
            "com.brave.browser:id/url_bar",
            "com.microsoft.emmx:id/url_bar"
        )
        for (id in candidateIds) {
            try {
                val nodes = rootNode.findAccessibilityNodeInfosByViewId(id)
                if (!nodes.isNullOrEmpty()) {
                    val text = nodes[0].text?.toString()
                    if (!text.isNullOrBlank()) {
                        return cleanDomain(text)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        // Fallback: search top-level EditText nodes
        return findDomainInNode(rootNode, 0)
    }

    private fun findDomainInNode(node: AccessibilityNodeInfo, depth: Int): String? {
        if (depth > 4) return null
        try {
            if (node.className == "android.widget.EditText") {
                val text = node.text?.toString()
                if (!text.isNullOrBlank() && text.contains(".")) {
                    val cleaned = cleanDomain(text)
                    if (cleaned.contains(".")) return cleaned
                }
            }
            val count = node.childCount
            for (i in 0 until count) {
                val child = node.getChild(i)
                if (child != null) {
                    val found = findDomainInNode(child, depth + 1)
                    if (found != null) return found
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return null
    }

    private fun cleanDomain(rawText: String): String {
        var text = rawText.trim().lowercase()
        if (text.startsWith("https://")) text = text.removePrefix("https://")
        if (text.startsWith("http://")) text = text.removePrefix("http://")
        if (text.startsWith("www.")) text = text.removePrefix("www.")
        text = text.substringBefore("/")
        text = text.substringBefore("?")
        text = text.substringBefore(":")
        return text.trim()
    }

    private fun launchBlockingScreen(target: String, serviceName: String, category: String) {
        val intent = Intent(this, AccessBlockedActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TARGET", target)
            putExtra("EXTRA_SERVICE_NAME", serviceName)
            putExtra("EXTRA_CATEGORY", category)
        }
        startActivity(intent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
