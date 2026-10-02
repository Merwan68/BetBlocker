package com.example

import com.example.data.InitialGamblingCatalog
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogAndDomainMatchingTest {

    private fun isDomainBlockedMock(host: String, blockedSet: Set<String>): Boolean {
        val cleanHost = host.trim().lowercase().removePrefix("www.")
        if (cleanHost.isBlank()) return false

        var current = cleanHost
        while (current.isNotBlank() && current.contains(".")) {
            if (blockedSet.contains(current)) return true
            current = current.substringAfter(".", "")
        }
        return false
    }

    @Test
    fun testRequestedBettingDomainsPresent() {
        val domains = InitialGamblingCatalog.defaultDomains.map { it.domain }.toSet()

        // Verify specific user-requested betting platforms
        assertTrue("dash.bet should be in catalog", domains.contains("dash.bet"))
        assertTrue("dashbet.com should be in catalog", domains.contains("dashbet.com"))
        assertTrue("dashbet.et should be in catalog", domains.contains("dashbet.et"))

        assertTrue("melbet.com should be in catalog", domains.contains("melbet.com"))
        assertTrue("melbet.org should be in catalog", domains.contains("melbet.org"))
        assertTrue("melbet.et should be in catalog", domains.contains("melbet.et"))

        assertTrue("zplaybet.xyz should be in catalog", domains.contains("zplaybet.xyz"))
        assertTrue("zplay.bet should be in catalog", domains.contains("zplay.bet"))

        assertTrue("1xbet.com should be in catalog", domains.contains("1xbet.com"))
        assertTrue("1xbet.et should be in catalog", domains.contains("1xbet.et"))

        assertTrue("mbs.bet should be in catalog", domains.contains("mbs.bet"))
        assertTrue("habeshabet.com should be in catalog", domains.contains("habeshabet.com"))
        assertTrue("vamos.bet should be in catalog", domains.contains("vamos.bet"))
        assertTrue("hulusport.com should be in catalog", domains.contains("hulusport.com"))
    }

    @Test
    fun testSubdomainResolution() {
        val catalogDomains = InitialGamblingCatalog.defaultDomains.map { it.domain }.toSet()

        assertTrue("m.dash.bet should match dash.bet", isDomainBlockedMock("m.dash.bet", catalogDomains))
        assertTrue("sports.melbet.com should match melbet.com", isDomainBlockedMock("sports.melbet.com", catalogDomains))
        assertTrue("live.zplaybet.xyz should match zplaybet.xyz", isDomainBlockedMock("live.zplaybet.xyz", catalogDomains))
        assertTrue("aff.1xbet.com should match 1xbet.com", isDomainBlockedMock("aff.1xbet.com", catalogDomains))
        assertTrue("mobile.mbs.bet should match mbs.bet", isDomainBlockedMock("mobile.mbs.bet", catalogDomains))
    }
}
