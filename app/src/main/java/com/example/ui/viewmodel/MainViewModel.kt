package com.example.ui.viewmodel

import android.app.Application
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.BlockEventEntity
import com.example.data.model.BlockedAppEntity
import com.example.data.model.BlockedDomainEntity
import com.example.data.model.CategoryCount
import com.example.data.repository.BlockingRepository
import com.example.security.PinManager
import com.example.security.ProtectionLevel
import com.example.service.DnsVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isProtectionActive: Boolean = true,
    val protectionLevel: ProtectionLevel = ProtectionLevel.STRONG,
    val protectedDays: Int = 1,
    val blockedToday: Int = 0,
    val blockedThisWeek: Int = 0,
    val totalBlocked: Int = 0,
    val totalWebsites: Int = 0,
    val totalApps: Int = 0,
    val isPinSet: Boolean = false,
    val isAccountabilityEnabled: Boolean = false,
    val accountabilityName: String = "",
    val accountabilityContact: String = "",
    val isUnderCooldown: Boolean = false,
    val remainingCooldownMinutes: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = BlockingRepository(
        db.blockedDomainDao(),
        db.blockedAppDao(),
        db.blockEventDao(),
        db.syncMetadataDao()
    )
    val pinManager = PinManager(application)

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            isProtectionActive = pinManager.isProtectionEnabled,
            protectionLevel = pinManager.protectionLevel,
            protectedDays = pinManager.protectedDaysCount,
            isPinSet = pinManager.isPinSet(),
            isAccountabilityEnabled = pinManager.isAccountabilityEnabled,
            accountabilityName = pinManager.accountabilityName,
            accountabilityContact = pinManager.accountabilityContact,
            isUnderCooldown = pinManager.isUnderCooldown,
            remainingCooldownMinutes = pinManager.remainingCooldownMinutes
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val allDomains: StateFlow<List<BlockedDomainEntity>> = repository.allDomainsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApps: StateFlow<List<BlockedAppEntity>> = repository.allAppsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentEvents: StateFlow<List<BlockEventEntity>> = repository.recentEventsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoryCounts: StateFlow<List<CategoryCount>> = repository.categoryCountsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Collect reactive counters and update UI state
        viewModelScope.launch {
            combine(
                repository.getBlockedTodayFlow(),
                repository.getBlockedThisWeekFlow(),
                repository.totalBlockedCountFlow,
                repository.activeDomainCountFlow,
                repository.activeAppCountFlow
            ) { today, thisWeek, total, activeDomains, activeApps ->
                _uiState.value.copy(
                    blockedToday = today,
                    blockedThisWeek = thisWeek,
                    totalBlocked = total,
                    totalWebsites = activeDomains,
                    totalApps = activeApps,
                    isProtectionActive = pinManager.isProtectionEnabled,
                    protectionLevel = pinManager.protectionLevel,
                    protectedDays = pinManager.protectedDaysCount,
                    isPinSet = pinManager.isPinSet(),
                    isAccountabilityEnabled = pinManager.isAccountabilityEnabled,
                    isUnderCooldown = pinManager.isUnderCooldown,
                    remainingCooldownMinutes = pinManager.remainingCooldownMinutes
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun toggleProtection(pin: String? = null, onResult: (Boolean, String?) -> Unit) {
        val current = pinManager.isProtectionEnabled
        if (current) {
            // Turning OFF requires PIN if set
            if (pinManager.isPinSet()) {
                if (pin == null || !pinManager.verifyPin(pin)) {
                    onResult(false, "Incorrect PIN. Protection cannot be deactivated.")
                    return
                }
            }
            // Strict mode cooldown check
            if (pinManager.protectionLevel == ProtectionLevel.STRICT) {
                if (!pinManager.isUnderCooldown) {
                    pinManager.startCooldown(15)
                    _uiState.value = _uiState.value.copy(
                        isUnderCooldown = true,
                        remainingCooldownMinutes = 15
                    )
                    onResult(false, "Strict Mode Cooldown Active. 15-minute reflection period started.")
                    return
                }
            }

            pinManager.isProtectionEnabled = false
            DnsVpnService.stop(getApplication())
            _uiState.value = _uiState.value.copy(isProtectionActive = false)
            onResult(true, "Protection Deactivated.")
        } else {
            // Turning ON
            pinManager.isProtectionEnabled = true
            val vpnPrepareIntent = VpnService.prepare(getApplication())
            if (vpnPrepareIntent == null) {
                DnsVpnService.start(getApplication())
            }
            _uiState.value = _uiState.value.copy(
                isProtectionActive = true,
                protectedDays = pinManager.protectedDaysCount
            )
            onResult(true, "Protection Activated.")
        }
    }

    fun setProtectionLevel(level: ProtectionLevel, pin: String? = null, onResult: (Boolean, String?) -> Unit) {
        if (pinManager.isPinSet() && (pin == null || !pinManager.verifyPin(pin))) {
            onResult(false, "Incorrect PIN.")
            return
        }
        pinManager.protectionLevel = level
        _uiState.value = _uiState.value.copy(protectionLevel = level)
        onResult(true, "Protection level updated to ${level.title}")
    }

    fun setupPin(newPin: String): Boolean {
        val success = pinManager.setPin(newPin)
        if (success) {
            _uiState.value = _uiState.value.copy(isPinSet = true)
        }
        return success
    }

    fun verifyPin(pin: String): Boolean = pinManager.verifyPin(pin)

    fun clearPin(currentPin: String): Boolean {
        if (!pinManager.verifyPin(currentPin)) return false
        val success = pinManager.clearPin()
        if (success) {
            _uiState.value = _uiState.value.copy(isPinSet = false)
        }
        return success
    }

    fun addCustomDomain(domain: String, serviceName: String, category: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addCustomDomain(domain, serviceName, category)
            onComplete()
        }
    }

    fun toggleDomain(domain: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleDomainStatus(domain, isActive)
        }
    }

    fun removeDomain(domain: String) {
        viewModelScope.launch {
            repository.removeCustomDomain(domain)
        }
    }

    fun addCustomApp(packageName: String, appName: String, category: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addCustomApp(packageName, appName, category)
            onComplete()
        }
    }

    fun toggleApp(packageName: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAppStatus(packageName, isActive)
        }
    }

    fun removeApp(packageName: String) {
        viewModelScope.launch {
            repository.removeCustomApp(packageName)
        }
    }

    fun updateAccountability(enabled: Boolean, name: String, contact: String) {
        pinManager.isAccountabilityEnabled = enabled
        pinManager.accountabilityName = name
        pinManager.accountabilityContact = contact
        _uiState.value = _uiState.value.copy(
            isAccountabilityEnabled = enabled,
            accountabilityName = name,
            accountabilityContact = contact
        )
    }

    fun clearHistory() {
        viewModelScope.launch {
            db.blockEventDao().clearAllEvents()
        }
    }
}
