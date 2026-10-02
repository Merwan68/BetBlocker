package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccessibilityServiceLogicTest {

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

    @Test
    fun testBrowserUrlCleaning() {
        assertEquals("bet365.com", cleanDomain("https://www.bet365.com/sports/football"))
        assertEquals("stake.com", cleanDomain("http://stake.com/casino?game=blackjack"))
        assertEquals("draftkings.com", cleanDomain("draftkings.com:443/lobby"))
        assertEquals("roobet.com", cleanDomain("www.roobet.com"))
    }

    @Test
    fun testIgnoredPackages() {
        val ignoredPackages = setOf(
            "com.android.systemui",
            "com.google.android.apps.nexuslauncher",
            "com.android.launcher3"
        )

        assertTrue(ignoredPackages.contains("com.android.systemui"))
        assertFalse(ignoredPackages.contains("com.draftkings.sportsbook"))
        assertFalse(ignoredPackages.contains("com.fanduel.sportsbook"))
    }

    @Test
    fun testFastSetLookup() {
        val blockedPackages = setOf(
            "com.draftkings.sportsbook",
            "com.fanduel.sportsbook",
            "com.betmgm.sports",
            "com.pokerstars.net",
            "com.stake.mobile"
        )

        assertTrue("Should detect DraftKings", blockedPackages.contains("com.draftkings.sportsbook"))
        assertTrue("Should detect Stake", blockedPackages.contains("com.stake.mobile"))
        assertFalse("Should allow harmless calculator", blockedPackages.contains("com.google.android.calculator"))
    }
}
