package com.example.service

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.view.accessibility.AccessibilityManager

object AccessibilityHelper {

    /**
     * Checks if BetShield's AppBlockerAccessibilityService is actively enabled in Android settings.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        if (AppBlockerAccessibilityService.isConnected) return true

        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        val expectedComponent = ComponentName(context, AppBlockerAccessibilityService::class.java).flattenToString()

        return enabledServices.any { service ->
            service.id.equals(expectedComponent, ignoreCase = true) ||
                    service.resolveInfo?.serviceInfo?.packageName == context.packageName
        }
    }

    /**
     * Scans installed applications on the device and returns packages that match known gambling apps.
     */
    fun getInstalledGamblingApps(context: Context, knownGamblingPackages: Set<String>): List<InstalledGamblingApp> {
        val pm = context.packageManager
        val installedApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledApplications(0)
        }

        val results = mutableListOf<InstalledGamblingApp>()

        for (appInfo in installedApps) {
            // Exclude system apps
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) continue

            val pkg = appInfo.packageName
            if (knownGamblingPackages.contains(pkg)) {
                val label = pm.getApplicationLabel(appInfo).toString()
                results.add(InstalledGamblingApp(packageName = pkg, appName = label))
            }
        }
        return results
    }
}

data class InstalledGamblingApp(
    val packageName: String,
    val appName: String
)
