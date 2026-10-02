package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.VpnService
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.service.DnsVpnService
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SecurityScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.WebsitesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

enum class MainNavigationTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("Shield", Icons.Default.Shield),
    WEBSITES("Websites", Icons.Default.Language),
    APPS("Apps", Icons.Default.Apps),
    SECURITY("Security", Icons.Default.Lock),
    STATS("Stats", Icons.Default.BarChart)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@Composable
fun MainAppRoot(viewModel: MainViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf(MainNavigationTab.DASHBOARD) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allDomains by viewModel.allDomains.collectAsStateWithLifecycle()
    val allApps by viewModel.allApps.collectAsStateWithLifecycle()
    val recentEvents by viewModel.recentEvents.collectAsStateWithLifecycle()
    val categoryCounts by viewModel.categoryCounts.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(uiState.isProtectionActive) {
        if (uiState.isProtectionActive && VpnService.prepare(context) == null) {
            DnsVpnService.start(context)
        }
    }

    // Handle Android system back gesture to return to Dashboard if in sub-screen
    BackHandler(enabled = currentTab != MainNavigationTab.DASHBOARD) {
        currentTab = MainNavigationTab.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("main_navigation_bar")) {
                MainNavigationTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavigationTab.DASHBOARD -> DashboardScreen(
                    state = uiState,
                    onToggleProtection = { pin, onResult ->
                        viewModel.toggleProtection(pin) { success, msg ->
                            onResult(success, msg)
                            msg?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
                        }
                    },
                    onNavigateToWebsites = { currentTab = MainNavigationTab.WEBSITES },
                    onNavigateToApps = { currentTab = MainNavigationTab.APPS },
                    onNavigateToSecurity = { currentTab = MainNavigationTab.SECURITY },
                    onNavigateToStats = { currentTab = MainNavigationTab.STATS }
                )

                MainNavigationTab.WEBSITES -> WebsitesScreen(
                    domains = allDomains,
                    onToggleDomain = { domain, isActive -> viewModel.toggleDomain(domain, isActive) },
                    onAddCustomDomain = { domain, name, category, onComplete ->
                        viewModel.addCustomDomain(domain, name, category) {
                            onComplete()
                            scope.launch { snackbarHostState.showSnackbar("Added $domain to blocklist") }
                        }
                    },
                    onRemoveDomain = { domain ->
                        viewModel.removeDomain(domain)
                        scope.launch { snackbarHostState.showSnackbar("Removed $domain") }
                    },
                    onBack = { currentTab = MainNavigationTab.DASHBOARD }
                )

                MainNavigationTab.APPS -> AppsScreen(
                    apps = allApps,
                    onToggleApp = { pkg, isActive -> viewModel.toggleApp(pkg, isActive) },
                    onAddCustomApp = { pkg, name, category, onComplete ->
                        viewModel.addCustomApp(pkg, name, category) {
                            onComplete()
                            scope.launch { snackbarHostState.showSnackbar("Added $name to blocklist") }
                        }
                    },
                    onRemoveApp = { pkg ->
                        viewModel.removeApp(pkg)
                        scope.launch { snackbarHostState.showSnackbar("Removed custom app rule") }
                    },
                    onBack = { currentTab = MainNavigationTab.DASHBOARD }
                )

                MainNavigationTab.SECURITY -> SecurityScreen(
                    state = uiState,
                    onSetProtectionLevel = { level, pin, onResult ->
                        viewModel.setProtectionLevel(level, pin) { success, msg ->
                            onResult(success, msg)
                            msg?.let { scope.launch { snackbarHostState.showSnackbar(it) } }
                        }
                    },
                    onSetupPin = { pin ->
                        val ok = viewModel.setupPin(pin)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (ok) "Protection PIN successfully created!" else "Failed to save PIN"
                            )
                        }
                        ok
                    },
                    onClearPin = { pin ->
                        val ok = viewModel.clearPin(pin)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (ok) "Protection PIN removed" else "Incorrect current PIN"
                            )
                        }
                        ok
                    },
                    onUpdateAccountability = { enabled, name, contact ->
                        viewModel.updateAccountability(enabled, name, contact)
                        scope.launch { snackbarHostState.showSnackbar("Accountability settings saved") }
                    },
                    onBack = { currentTab = MainNavigationTab.DASHBOARD }
                )

                MainNavigationTab.STATS -> StatisticsScreen(
                    state = uiState,
                    recentEvents = recentEvents,
                    categoryCounts = categoryCounts,
                    onClearHistory = {
                        viewModel.clearHistory()
                        scope.launch { snackbarHostState.showSnackbar("Audit history cleared") }
                    },
                    onBack = { currentTab = MainNavigationTab.DASHBOARD }
                )
            }
        }
    }
}
