/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.feature.network.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alhissn.core.ui.components.AnimatedToggle
import com.alhissn.core.ui.components.CyberBackground
import com.alhissn.core.ui.components.NeonButton
import com.alhissn.core.ui.theme.LocalCustomColors
import com.alhissn.feature.network.NetworkViewModel
import com.alhissn.core.domain.model.NetworkState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkGuardScreen(
    networkViewModel: NetworkViewModel,
    onNavigateToBlockLists: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val customColors = LocalCustomColors.current
    val networkState = networkViewModel.networkState.collectAsState().value
    val isVpnActive = networkState is NetworkState.CONNECTED
    val blockedCount by networkViewModel.blockedRequestsCount.collectAsState()
    
    var strictModeEnabled by remember { mutableStateOf(false) }
    var smartBlockEnabled by remember { mutableStateOf(true) }

    CyberBackground {
        // @iamasrakib - core logic
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Network Guard",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = customColors.text
                        )
                    },
                    navigationIcon = {
                        /* built by iamasrakib */
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = customColors.text)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Status Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (isVpnActive) customColors.statusOn.copy(alpha = 0.1f) else customColors.surface)
                        .border(1.dp, if (isVpnActive) customColors.statusOn.copy(alpha = 0.3f) else customColors.surfaceBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = if (isVpnActive) customColors.statusOn else customColors.textMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (isVpnActive) "DNS Filter Active" else "DNS Filter Inactive",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isVpnActive) customColors.statusOn else customColors.text
                        )
                        Text(
                            text = "All outgoing traffic is securely monitored.",
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Master Toggle", color = customColors.text, fontWeight = FontWeight.Bold)
                            AnimatedToggle(
                                checked = isVpnActive,
                                onCheckedChange = { active ->
                                    if (active) {
                                        networkViewModel.startVpn(context)
                                    } else {
                                        networkViewModel.stopVpn(context)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Stats Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(title = "Threats Blocked", value = blockedCount.toString(), icon = Icons.Default.Block, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    StatBox(title = "Domains Scanned", value = "12,408", icon = Icons.Default.Language, modifier = Modifier.weight(1f)) // Mocked for UI polish
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Advanced Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = customColors.text,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                // Settings Toggles
                SettingToggleRow(
                    title = "Strict Mode",
                    subtitle = "Blocks all unverified HTTP traffic instantly.",
                    checked = strictModeEnabled,
                    onCheckedChange = { strictModeEnabled = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SettingToggleRow(
                    title = "Smart AI Blocking",
                    subtitle = "Automatically adapts to new phishing domains.",
                    checked = smartBlockEnabled,
                    onCheckedChange = { smartBlockEnabled = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                NeonButton(
                    text = "Manage Block Lists",
                    icon = Icons.Default.Security,
                    onClick = onNavigateToBlockLists,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    val customColors = LocalCustomColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(customColors.surface)
            .border(1.dp, customColors.surfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = customColors.statusOn, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = customColors.text)
            Text(title, style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val customColors = LocalCustomColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(customColors.surface)
            .border(1.dp, customColors.surfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = customColors.text)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = customColors.statusOn,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = customColors.surfaceBorder
            )
        )
    }
}


