/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.core.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

val CyberCutShape = CutCornerShape(16.dp)

val AsymmetricShieldShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 0.dp,
    bottomStart = 0.dp,
    bottomEnd = 24.dp
)

// Code crafted by iamasrakib
class FuturisticHexagonShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val width = size.width
            val height = size.height
            val cut = 24f * density.density

            moveTo(cut, 0f)
            lineTo(width - cut, 0f)
            lineTo(width, cut)
            lineTo(width, height - cut)
            lineTo(width - cut, height)
            lineTo(cut, height)
            lineTo(0f, height - cut)
            lineTo(0f, cut)
            close()
        }
        return Outline.Generic(path)
    }
}


