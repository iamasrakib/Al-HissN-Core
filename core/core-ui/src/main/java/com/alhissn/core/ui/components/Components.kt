/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.ui.components

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
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alhissn.core.ui.theme.LocalCustomColors
import com.alhissn.core.ui.theme.NeonBlue
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun CyberBackground(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val customColors = LocalCustomColors.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(customColors.bg, Color(0xFF020205))
                )
            ),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}

@Composable
fun ShieldIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 5.dp.toPx()
        val innerStrokeWidth = 3.dp.toPx()
        val circuitStrokeWidth = 2.dp.toPx()
        val dotRadius = 4.dp.toPx()

        val outerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.20f)
            cubicTo(w * 0.60f, h * 0.25f, w * 0.75f, h * 0.32f, w * 0.85f, h * 0.32f)
            cubicTo(w * 0.85f, h * 0.60f, w * 0.70f, h * 0.85f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.30f, h * 0.85f, w * 0.15f, h * 0.60f, w * 0.15f, h * 0.32f)
            cubicTo(w * 0.25f, h * 0.32f, w * 0.40f, h * 0.25f, w * 0.5f, h * 0.20f)
            close()
        }

        drawPath(
            path = outerPath,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )

        val innerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.26f)
            cubicTo(w * 0.60f, h * 0.30f, w * 0.72f, h * 0.36f, w * 0.80f, h * 0.36f)
            cubicTo(w * 0.80f, h * 0.60f, w * 0.68f, h * 0.82f, w * 0.5f, h * 0.90f)
            cubicTo(w * 0.32f, h * 0.82f, w * 0.20f, h * 0.60f, w * 0.20f, h * 0.36f)
            cubicTo(w * 0.28f, h * 0.36f, w * 0.40f, h * 0.30f, w * 0.5f, h * 0.26f)
            close()
        }

        drawPath(
            path = innerPath,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = innerStrokeWidth,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )

        val circuitPath = Path().apply {
            // @iamasrakib - core logic
            // Upper wire
            moveTo(w * 0.47f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.38f)
            lineTo(w * 0.57f, h * 0.41f)
            lineTo(w * 0.64f, h * 0.41f)

            // Lower wire
            moveTo(w * 0.50f, h * 0.43f)
            lineTo(w * 0.54f, h * 0.43f)
            lineTo(w * 0.57f, h * 0.46f)
            lineTo(w * 0.60f, h * 0.43f)
            lineTo(w * 0.66f, h * 0.43f)

            // Branch wire
            moveTo(w * 0.60f, h * 0.41f)
            lineTo(w * 0.60f, h * 0.36f)
            lineTo(w * 0.64f, h * 0.36f)
        }

        drawPath(
            path = circuitPath,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = circuitStrokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round
            )
        )

        // Circuit Dots
        drawCircle(color, dotRadius, androidx.compose.ui.geometry.Offset(w * 0.64f, h * 0.41f))
        drawCircle(color, dotRadius, androidx.compose.ui.geometry.Offset(w * 0.66f, h * 0.43f))
        drawCircle(color, dotRadius, androidx.compose.ui.geometry.Offset(w * 0.64f, h * 0.36f))
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    val customColors = LocalCustomColors.current
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.8f),
                spotColor = customColors.neonCyan.copy(alpha = 0.1f)
            )
            .background(
                color = customColors.glass,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = customColors.surfaceBorder,
                shape = shape
            )
            .padding(16.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    isPrimary: Boolean = true
) {
    // @iamasrakib - core logic
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

    val baseColor = if (isPrimary) customColors.neonCyan else customColors.glass

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isPrimary) baseColor.copy(alpha = 0.8f) else Color.Black
            )
            .background(
                brush = if (isPrimary) Brush.horizontalGradient(listOf(NeonBlue, customColors.neonCyan)) else Brush.linearGradient(listOf(baseColor, baseColor)),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isPrimary) customColors.neonCyan.copy(alpha = 0.5f) else customColors.surfaceBorder,
                shape = RoundedCornerShape(12.dp)
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
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                letterSpacing = 1.sp
            )
        }
    }
}

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
                Canvas(modifier = Modifier.size(300.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                animatedColor.copy(alpha = currentGlowAlpha),
                                Color.Transparent
                            )
                        ),
                        radius = size.width / 2f
                    )
                }
                // Custom drawn digital shield fallback or procedural icon
                ShieldIcon(
                    color = animatedColor,
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}


