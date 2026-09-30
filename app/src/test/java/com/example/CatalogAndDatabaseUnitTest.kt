package com.example

import com.example.data.InitialGamblingCatalog
import com.example.data.model.GamblingCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogAndDatabaseUnitTest {

    @Test
    fun testInitialCatalogDomainsIntegrity() {
        val domains = InitialGamblingCatalog.defaultDomains
        assertTrue("Catalog must contain preloaded domains", domains.isNotEmpty())

        for (domainEntity in domains) {
            assertFalse("Domain should not be blank", domainEntity.domain.isBlank())
            assertFalse("Service name should not be blank", domainEntity.serviceName.isBlank())
            assertTrue("Domain should contain a dot", domainEntity.domain.contains("."))
            assertFalse("Domain should not contain http:// prefix", domainEntity.domain.startsWith("http://"))
            assertFalse("Domain should not contain https:// prefix", domainEntity.domain.startsWith("https://"))
            assertFalse("Domain should not contain path slashes", domainEntity.domain.contains("/"))
            assertTrue("Default domain should be active", domainEntity.isActive)
            assertFalse("Default domain should not be marked custom", domainEntity.isCustom)
            assertFalse("Default domain should not be marked deleted", domainEntity.isDeleted)

            val parsedCategory = GamblingCategory.fromString(domainEntity.category)
            assertNotNull("Category must be recognizable", parsedCategory)
        }
    }

    @Test
    fun testInitialCatalogAppsIntegrity() {
        val apps = InitialGamblingCatalog.defaultApps
        assertTrue("Catalog must contain preloaded apps", apps.isNotEmpty())

        for (app in apps) {
            assertFalse("Package name should not be blank", app.packageName.isBlank())
            assertFalse("App name should not be blank", app.appName.isBlank())
            assertTrue("Package name should contain at least one dot", app.packageName.contains("."))
            assertTrue("Default app should be active", app.isActive)
            assertFalse("Default app should not be custom", app.isCustom)
            assertFalse("Default app should not be deleted", app.isDeleted)
        }
    }

    @Test
    fun testDomainCleaningAndSubdomainLogic() {
        val rawInput = "https://www.BET365.com/sports"
        val cleanDomain = rawInput
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore("/")
            .trim()
            .lowercase()
            .removePrefix("www.")

        assertEquals("bet365.com", cleanDomain)

        // Subdomain extraction check
        val subdomain = "live.casino.stake.com"
        val parts = subdomain.split(".")
        val rootDomain = parts.takeLast(2).joinToString(".")
        assertEquals("stake.com", rootDomain)
    }

    @Test
    fun testGamblingCategoryResolution() {
        assertEquals(GamblingCategory.SPORTS_BETTING, GamblingCategory.fromString("SPORTS_BETTING"))
        assertEquals(GamblingCategory.SPORTS_BETTING, GamblingCategory.fromString("Sports Betting"))
        assertEquals(GamblingCategory.CRYPTO_GAMBLING, GamblingCategory.fromString("Cryptocurrency Gambling"))
        assertEquals(GamblingCategory.ONLINE_CASINO, GamblingCategory.fromString("Online Casino"))
        assertEquals(GamblingCategory.OTHER_GAMBLING, GamblingCategory.fromString("Unknown Category"))
    }
}
