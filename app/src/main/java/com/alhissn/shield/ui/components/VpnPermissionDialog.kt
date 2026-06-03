/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.alhissn.core.ui.theme.LocalCustomColors

/**
 * Compliance-friendly Glassmorphic VPN Permission Dialog.
 * Explains VPN usage clearly to satisfy user transparency requirements (Google Play Compliant).
 */
@Composable
fun VpnPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val customColors = LocalCustomColors.current

    // Pulsing shield animation for aesthetic engagement
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    Dialog(
        onDismissRequest = {}, // Empty lambda prevents dismissing by tapping outside or pressing back
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(customColors.surface)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(customColors.border, customColors.border.copy(alpha = 0.1f))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Shield animation container
                Box(
                    modifier = Modifier
                        .size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsing ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize(pulseScale)
                            .background(
                                color = customColors.statusOn.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = customColors.statusOn.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )

                    ShieldIcon(
                        color = customColors.statusOn,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "VPN TUNNEL PERMISSION",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Al HissN creates a local VPN tunnel to analyze and filter DNS traffic directly on your device.\n\n• Blocks ad trackers, malware, gambling, and adult sites.\n• Offline-first & privacy-first parsing.\n• No personal logs are created or transmitted.",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Start
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(customColors.border, customColors.border)
                            )
                            )
                    ) {
                        // Code crafted by iamasrakib
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    NeonButton(
                        text = "ACTIVATE SHIELD",
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                    )
                }
            }
        }
    }
}


