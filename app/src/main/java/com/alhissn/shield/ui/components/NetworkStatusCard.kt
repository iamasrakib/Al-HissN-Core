/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.ui.components

import android.app.Activity
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale
import com.alhissn.core.ui.theme.LocalCustomColors
import com.alhissn.core.domain.model.NetworkState
import com.alhissn.feature.network.NetworkViewModel

/**
 * Premium glassmorphic status card for VPN state visualization and toggling.
 */
@Composable
fun NetworkStatusCard(
    viewModel: NetworkViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalCustomColors.current

    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val blockedCount by viewModel.blockedRequestsCount.collectAsStateWithLifecycle()
    val uptimeMs by viewModel.uptimeMs.collectAsStateWithLifecycle()

    var showPermissionDialog by remember { mutableStateOf(false) }
    val isConnected = networkState is NetworkState.CONNECTED

    val uptimeString = remember(uptimeMs) {
        // Code crafted by iamasrakib
        val uptimeSeconds = uptimeMs / 1000
        val h = uptimeSeconds / 3600
        val m = (uptimeSeconds % 3600) / 60
        val s = uptimeSeconds % 60
        String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
    }

    // Permission Activity Launcher
    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // @iamasrakib - core logic
            viewModel.startVpn(context)
        }
    }

    val onCardClick = {
        if (isConnected || networkState is NetworkState.CONNECTING) {
            viewModel.stopVpn(context)
        } else {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                showPermissionDialog = true
            } else {
                viewModel.startVpn(context)
            }
        }
    }

    if (showPermissionDialog) {
        VpnPermissionDialog(
            onConfirm = {
                showPermissionDialog = false
                val prepareIntent = VpnService.prepare(context)
                if (prepareIntent != null) {
                    vpnLauncher.launch(prepareIntent)
                } else {
                    viewModel.startVpn(context)
                }
            },
            onDismiss = { showPermissionDialog = false }
        )
    }

    val (statusLabel, statusColor) = when (networkState) {
        is NetworkState.CONNECTED -> Pair("PROTECTION ON", Color(0xFF00F5A0))
        is NetworkState.CONNECTING -> Pair("ENCRYPTING...", Color(0xFFFFC107))
        is NetworkState.ERROR -> Pair("SHIELD FAULT", customColors.statusOff)
        is NetworkState.DISCONNECTED, is NetworkState.IDLE -> Pair("PROTECTION OFF", customColors.statusOff)
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onCardClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Connection State Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = statusColor.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse_state")
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_alpha"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color = statusColor.copy(alpha = alpha), shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = statusColor
                    )
                }

                Text(
                    text = if (isConnected) "Vpn Connection Secure" else "System-wide Traffic Unsecured",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Stats details row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column {
                        Text(
                            text = "UPTIME",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (isConnected) uptimeString else "00:00:00",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Column {
                        Text(
                            text = "BLOCKED THREATS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = blockedCount.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (blockedCount > 0) customColors.statusOff else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Radial icon button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(statusColor.copy(alpha = 0.12f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .border(1.dp, statusColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Filled.Shield else Icons.Outlined.Shield,
                    contentDescription = "VPN Active State Indicator",
                    tint = statusColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


