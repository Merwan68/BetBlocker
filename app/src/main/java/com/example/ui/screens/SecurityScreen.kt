package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.ProtectionLevel
import com.example.ui.viewmodel.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    state: DashboardUiState,
    onSetProtectionLevel: (ProtectionLevel, String?, (Boolean, String?) -> Unit) -> Unit,
    onSetupPin: (String) -> Boolean,
    onClearPin: (String) -> Boolean,
    onUpdateAccountability: (Boolean, String, String) -> Unit,
    onBack: () -> Unit
) {
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showClearPinDialog by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    var accountabilityName by remember(state.accountabilityName) { mutableStateOf(state.accountabilityName) }
    var accountabilityContact by remember(state.accountabilityContact) { mutableStateOf(state.accountabilityContact) }
    var accountabilityEnabled by remember(state.isAccountabilityEnabled) { mutableStateOf(state.isAccountabilityEnabled) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security & PIN", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("security_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            statusMessage?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(12.dp)
                ) {
                    Text(it, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Protection Levels Section
            Text(
                text = "Protection Level",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose how strictly BetShield enforces self-exclusion on this device.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProtectionLevel.entries.forEach { level ->
                val isSelected = state.protectionLevel == level
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onSetProtectionLevel(level, null) { success, msg ->
                                statusMessage = msg
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = level.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = level.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Protection PIN Section
            Text(
                text = "Protection PIN Lock",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "A PIN prevents impulsive unblocking and tampering during moments of temptation.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (state.isPinSet) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (state.isPinSet) "PIN Protection ACTIVE" else "No PIN Configured",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (state.isPinSet) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.isPinSet) "Required to deactivate protection or change rules"
                                    else "Settings can be altered without password",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showSetPinDialog = true },
                            modifier = Modifier.weight(1f).testTag("setup_pin_button")
                        ) {
                            Text(if (state.isPinSet) "Change PIN" else "Set Up PIN")
                        }

                        if (state.isPinSet) {
                            OutlinedButton(
                                onClick = { showClearPinDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Remove PIN")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accountability Mode Section
            Text(
                text = "Accountability Partner Mode",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Optionally designate a trusted sponsor, spouse, or friend for recovery support.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.People, contentDescription = null, tint = Color(0xFF38BDF8))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Enable Partner Accountability",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Switch(
                            checked = accountabilityEnabled,
                            onCheckedChange = {
                                accountabilityEnabled = it
                                onUpdateAccountability(it, accountabilityName, accountabilityContact)
                            }
                        )
                    }

                    if (accountabilityEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = accountabilityName,
                            onValueChange = {
                                accountabilityName = it
                                onUpdateAccountability(accountabilityEnabled, it, accountabilityContact)
                            },
                            label = { Text("Trusted Partner Name") },
                            placeholder = { Text("e.g. Alex (Sponsor)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = accountabilityContact,
                            onValueChange = {
                                accountabilityContact = it
                                onUpdateAccountability(accountabilityEnabled, accountabilityName, it)
                            },
                            label = { Text("Partner Email / Phone") },
                            placeholder = { Text("partner@example.com") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "ℹ️ Privacy Promise: BetShield does NOT secretly upload or sell personal data. Information is stored strictly on your device.",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showSetPinDialog) {
        SetPinDialog(
            onDismiss = { showSetPinDialog = false },
            onSave = { pin ->
                val ok = onSetupPin(pin)
                if (ok) {
                    statusMessage = "Protection PIN successfully saved."
                    showSetPinDialog = false
                }
            }
        )
    }

    if (showClearPinDialog) {
        ClearPinDialog(
            onDismiss = { showClearPinDialog = false },
            onClear = { pin ->
                val ok = onClearPin(pin)
                if (ok) {
                    statusMessage = "Protection PIN removed."
                    showClearPinDialog = false
                }
            }
        )
    }
}

@Composable
fun SetPinDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Protection PIN") },
        text = {
            Column {
                Text("Enter a 4-to-6 digit PIN to lock protection settings:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 8) pin = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("New PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_pin_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPin,
                    onValueChange = { if (it.length <= 8) confirmPin = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Confirm PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("confirm_new_pin_input")
                )
                error?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pin.length < 4) {
                        error = "PIN must be at least 4 digits."
                        return@Button
                    }
                    if (pin != confirmPin) {
                        error = "PINs do not match."
                        return@Button
                    }
                    onSave(pin)
                },
                modifier = Modifier.testTag("save_pin_button")
            ) {
                Text("Save PIN")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ClearPinDialog(
    onDismiss: () -> Unit,
    onClear: (String) -> Unit
) {
    var currentPin by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Protection PIN") },
        text = {
            Column {
                Text("Enter your current PIN to remove protection lock:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currentPin,
                    onValueChange = { currentPin = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text("Current PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onClear(currentPin) }) {
                Text("Remove Lock")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
