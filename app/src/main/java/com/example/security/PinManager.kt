package com.example.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

enum class ProtectionLevel(val title: String, val description: String) {
    BASIC("Basic Shield", "Blocks known gambling websites across all browsers via local DNS filter."),
    STRONG("Strong Shield", "Blocks gambling websites and intercepts installed gambling & sports betting apps."),
    STRICT("Strict Shield", "Strong blocking plus PIN lock on settings and tamper delay protection.")
}

class PinManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "betshield_security_prefs"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SALT = "pin_salt"
        private const val KEY_PROTECTION_ENABLED = "protection_enabled"
        private const val KEY_PROTECTION_LEVEL = "protection_level"
        private const val KEY_PROTECTION_START_TIME = "protection_start_time"
        private const val KEY_ACCOUNTABILITY_ENABLED = "accountability_enabled"
        private const val KEY_ACCOUNTABILITY_NAME = "accountability_name"
        private const val KEY_ACCOUNTABILITY_CONTACT = "accountability_contact"
        private const val KEY_COOLDOWN_UNTIL = "cooldown_until"
    }

    fun isPinSet(): Boolean {
        return prefs.getString(KEY_PIN_HASH, null) != null
    }

    fun setPin(pin: String): Boolean {
        if (pin.length < 4) return false
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt)
            .apply()
        return true
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return true
        val salt = prefs.getString(KEY_PIN_SALT, null) ?: return false
        val computedHash = hashPin(pin, salt)
        return storedHash == computedHash
    }

    fun clearPin(): Boolean {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .apply()
        return true
    }

    var isProtectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_PROTECTION_ENABLED, true)
        set(value) {
            val editor = prefs.edit().putBoolean(KEY_PROTECTION_ENABLED, value)
            if (value && protectionStartTime == 0L) {
                editor.putLong(KEY_PROTECTION_START_TIME, System.currentTimeMillis())
            } else if (!value) {
                editor.putLong(KEY_PROTECTION_START_TIME, 0L)
            }
            editor.apply()
        }

    var protectionLevel: ProtectionLevel
        get() {
            val name = prefs.getString(KEY_PROTECTION_LEVEL, ProtectionLevel.STRONG.name)
            return runCatching { ProtectionLevel.valueOf(name ?: ProtectionLevel.STRONG.name) }
                .getOrDefault(ProtectionLevel.STRONG)
        }
        set(value) {
            prefs.edit().putString(KEY_PROTECTION_LEVEL, value.name).apply()
        }

    val protectionStartTime: Long
        get() = prefs.getLong(KEY_PROTECTION_START_TIME, 0L)

    val protectedDaysCount: Int
        get() {
            val start = protectionStartTime
            if (start == 0L) return 0
            val diffMs = System.currentTimeMillis() - start
            val days = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            return if (days == 0 && isProtectionEnabled) 1 else days
        }

    var isAccountabilityEnabled: Boolean
        get() = prefs.getBoolean(KEY_ACCOUNTABILITY_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_ACCOUNTABILITY_ENABLED, value).apply()

    var accountabilityName: String
        get() = prefs.getString(KEY_ACCOUNTABILITY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACCOUNTABILITY_NAME, value).apply()

    var accountabilityContact: String
        get() = prefs.getString(KEY_ACCOUNTABILITY_CONTACT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ACCOUNTABILITY_CONTACT, value).apply()

    fun startCooldown(minutes: Int = 15) {
        val until = System.currentTimeMillis() + (minutes * 60 * 1000L)
        prefs.edit().putLong(KEY_COOLDOWN_UNTIL, until).apply()
    }

    val isUnderCooldown: Boolean
        get() = System.currentTimeMillis() < prefs.getLong(KEY_COOLDOWN_UNTIL, 0L)

    val remainingCooldownMinutes: Int
        get() {
            val until = prefs.getLong(KEY_COOLDOWN_UNTIL, 0L)
            val diff = until - System.currentTimeMillis()
            return if (diff > 0) ((diff / 60000L) + 1).toInt() else 0
        }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    private fun hashPin(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = "$salt:$pin:betshield_secure"
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
