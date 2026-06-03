/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.ui.components

import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.accessibilityservice.AccessibilityServiceInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.alhissn.core.ui.theme.LocalCustomColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Lazy Permission Dialog â€” asks for permission only when a feature that needs it is turned on.
 */
@Composable
fun LazyPermissionDialog(
    permission: LazyPermission,
    visible: Boolean,
    onDismiss: () -> Unit,
    onGranted: () -> Unit
) {
    val context = LocalContext.current
    val customColors = LocalCustomColors.current

    // Smart pre-check: if already granted, skip dialog entirely
    var shouldShowDialog by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            // Immediate check â€” if already granted, fire onGranted and skip dialog
            if (permission.isGranted(context)) {
                onGranted()
                return@LaunchedEffect
            }
            // Otherwise show the dialog and start polling
            shouldShowDialog = true
            while (isActive && shouldShowDialog) {
                if (permission.isGranted(context)) {
                    shouldShowDialog = false
                    onGranted()
                    return@LaunchedEffect
                }
                delay(800)
            }
        } else {
            shouldShowDialog = false
        }
    }

    if (!shouldShowDialog) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
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
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(permission.tint.copy(alpha = 0.12f))
                        .border(
                            width = 1.dp,
                            color = permission.tint.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = permission.icon,
                        contentDescription = null,
                        tint = permission.tint,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Text(
                    text = permission.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Not Now")
                    }

                    Button(
                        onClick = { permission.launchSettings(context) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = permission.tint
                        )
                    ) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

/**
 * Represents a permission that can be requested lazily (when a feature is enabled).
 */
sealed class LazyPermission(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color
) {
    abstract fun isGranted(context: Context): Boolean
    abstract fun launchSettings(context: Context)

    object Accessibility : LazyPermission(
        title = "Accessibility Service Required",
        description = "Doom Scroll Blocker and Text Filter need Accessibility access to scan your screen and block harmful content in real-time.",
        icon = Icons.Rounded.Accessibility,
        tint = Color(0xFF4CAF50)
    ) {
        override fun isGranted(context: Context): Boolean = try {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            val services = am?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK) ?: emptyList()
            services.any {
                val info = it.resolveInfo.serviceInfo
                info.packageName == context.packageName && info.name == "com.alhissn.feature.accessibility.AlHissnScreenScanner"
            }
        } catch (_: Exception) { false }

        override fun launchSettings(context: Context) {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    object Overlay : LazyPermission(
        title = "Display Over Apps Required",
        description = "The blur overlay feature needs permission to draw over other apps to protect you from inappropriate content.",
        icon = Icons.Rounded.Security,
        tint = Color(0xFF2196F3)
    ) {
        override fun isGranted(context: Context): Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else true

        override fun launchSettings(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }

    object Vpn : LazyPermission(
        title = "VPN Permission Required",
        description = "Network filtering needs VPN access to monitor and block malicious connections before they reach your device.",
        icon = Icons.Rounded.VpnKey,
        tint = Color(0xFF9C27B0)
    ) {
        override fun isGranted(context: Context): Boolean =
            VpnService.prepare(context) == null

        override fun launchSettings(context: Context) {
            val intent = VpnService.prepare(context)
            if (intent != null) {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    object UsageStats : LazyPermission(
        title = "App Usage Access Required",
        description = "App blocking needs usage stats access to detect when blocked apps are opened and stop you from using them.",
        icon = Icons.Rounded.Analytics,
        tint = Color(0xFFFF9800)
    ) {
        override fun isGranted(context: Context): Boolean = try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            }
            mode == android.app.AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) { false }

        override fun launchSettings(context: Context) {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    object ManageStorage : LazyPermission(
        title = "All Files Access Required",
        description = "Deep storage scanning needs access to all files to detect threats hidden anywhere on your device.",
        icon = Icons.Rounded.Storage,
        tint = Color(0xFF795548)
    ) {
        override fun isGranted(context: Context): Boolean =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                android.os.Environment.isExternalStorageManager()
            } else {
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }

        override fun launchSettings(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = android.net.Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}


