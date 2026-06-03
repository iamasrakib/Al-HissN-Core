/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alhissn.core.ui.theme.LocalCustomColors

/**
 * Procedural ShieldIcon drawn on Canvas.
 * Solves dependency on static icons and provides a clean futuristic vector shield.
 */
@Composable
fun ShieldIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val outerPath = Path().apply {
            // @iamasrakib - core logic
            moveTo(w * 0.5f, h * 0.05f)
            quadraticTo(w * 0.75f, h * 0.02f, w * 0.95f, h * 0.2f)
            quadraticTo(w * 0.95f, h * 0.6f, w * 0.5f, h * 0.95f)
            quadraticTo(w * 0.05f, h * 0.6f, w * 0.05f, h * 0.2f)
            quadraticTo(w * 0.25f, h * 0.02f, w * 0.5f, h * 0.05f)
            close()
        }

        drawPath(
            path = outerPath,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
        )

        val innerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.12f)
            quadraticTo(w * 0.73f, h * 0.1f, w * 0.88f, h * 0.24f)
            quadraticTo(w * 0.88f, h * 0.57f, w * 0.5f, h * 0.88f)
            quadraticTo(w * 0.12f, h * 0.57f, w * 0.12f, h * 0.24f)
            quadraticTo(w * 0.27f, h * 0.1f, w * 0.5f, h * 0.12f)
            close()
        }

        drawPath(
            path = innerPath,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )

        val line1 = Path().apply {
            // @iamasrakib - core logic
            moveTo(w * 0.48f, h * 0.28f)
            lineTo(w * 0.65f, h * 0.28f)
            lineTo(w * 0.75f, h * 0.38f)
            lineTo(w * 0.80f, h * 0.38f)
        }
        drawPath(
            path = line1,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(w * 0.80f, h * 0.38f)
        )

        val line2 = Path().apply {
            moveTo(w * 0.58f, h * 0.32f)
            lineTo(w * 0.68f, h * 0.32f)
            lineTo(w * 0.76f, h * 0.44f)
            lineTo(w * 0.78f, h * 0.44f)
        }
        drawPath(
            path = line2,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
        )
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.44f)
        )
    }
}

/**
 * GlassCard mimics a premium translucent glass card.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    val customColors = LocalCustomColors.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.2f),
                spotColor = customColors.statusOn.copy(alpha = if (isDark) 0.2f else 0.1f)
            )
            .background(
                color = customColors.surface,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = customColors.border,
                shape = shape
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

/**
 * NeonButton is a premium button with scaling animations on click and a neon glow effect.
 */
@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val customColors = LocalCustomColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "neon_button_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(
                elevation = if (isPressed) 3.dp else 12.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = customColors.statusOn.copy(alpha = 0.6f)
            )
            .background(
                color = customColors.statusOn,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = Color.White),
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}

/**
 * AnimatedToggle is a fluid-sliding custom switch button with security lock icons.
 */
@Composable
fun AnimatedToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val thumbPosition by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "toggle_thumb_pos"
    )

    val trackColor = if (checked) customColors.statusOn else customColors.border

    Box(
        modifier = modifier
            .size(width = 64.dp, height = 36.dp)
            .background(color = trackColor, shape = RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .align(Alignment.CenterStart)
                .graphicsLayer {
                    val maxTranslation = (64.dp - 36.dp).toPx()
                    translationX = thumbPosition * maxTranslation
                }
                .shadow(4.dp, shape = CircleShape)
                .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (checked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = if (checked) customColors.statusOn else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * StatusPill details card with smooth expand-collapse content sizing.
 */
@Composable
fun StatusPill(
    statusText: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    detailsText: String? = null
) {
    val customColors = LocalCustomColors.current
    var expanded by remember { mutableStateOf(false) }
    val dotColor = if (isActive) customColors.statusOn else customColors.statusOff

    Box(
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .background(
                color = customColors.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = customColors.border,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = dotColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            if (expanded && detailsText != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = detailsText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * FloatingShieldFAB displays a custom circular button with a Lottie animation
 * (falling back to a procedurally drawn shield).
 */
@Composable
fun FloatingShieldFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets9.lottiefiles.com/packages/lf20_5n8y2s.json")
    )

    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = customColors.statusOn.copy(alpha = 0.5f)
            )
            .background(
                color = customColors.statusOn,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                iterations = Integer.MAX_VALUE,
                modifier = Modifier.size(36.dp)
            )
        } else {
            ShieldIcon(
                color = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Custom Compose Shape combining a Fortress (crenellations at top) and a Shield (pointed bottom).
 */
class ShieldShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.5f, h * 0.05f)
            quadraticTo(w * 0.75f, h * 0.02f, w * 0.95f, h * 0.2f)
            quadraticTo(w * 0.95f, h * 0.6f, w * 0.5f, h * 0.95f)
            quadraticTo(w * 0.05f, h * 0.6f, w * 0.05f, h * 0.2f)
            quadraticTo(w * 0.25f, h * 0.02f, w * 0.5f, h * 0.05f)
            close()
        }
        return Outline.Generic(path)
    }
}

class FortressShieldShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return ShieldShape().createOutline(size, layoutDirection, density)
    }
}

/**
 * Interactive glowing Fortress-Shield button that serves as the master toggle.
 */
@Composable
fun FortressShieldButton(
    isVpnActive: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalCustomColors.current
    val targetColor = if (isVpnActive) customColors.statusOn else customColors.statusOff
    val animatedColor by animateColorAsState(targetValue = targetColor, tween(500), label = "shield_color")

    val infiniteTransition = rememberInfiniteTransition(label = "shield_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val currentGlowAlpha = if (isVpnActive) glowAlpha else 0.2f
    val scale by animateFloatAsState(targetValue = if (isVpnActive) 1.05f else 1f, tween(300), label = "shield_scale")

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onToggle(!isVpnActive) }
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(260.dp)) {
                    drawCircle(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                animatedColor.copy(alpha = currentGlowAlpha),
                                Color.Transparent
                            )
                        ),
                        radius = size.width / 2f
                    )
                }
                DigitalShieldIcon(
                    color = animatedColor,
                    modifier = Modifier.size(200.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isVpnActive) "SECURED" else "INACTIVE",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Black),
                color = animatedColor
            )
        }
    }
}

/**
 * A custom icon that replicates a Digital Shield with circuit traces.
 * Uses the user's custom image mask and tints it based on status.
 */
@Composable
fun DigitalShieldIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Image(
        painter = androidx.compose.ui.res.painterResource(id = com.alhissn.shield.R.drawable.custom_shield_mask),
        contentDescription = "Shield Icon",
        modifier = modifier,
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(color)
    )
}


