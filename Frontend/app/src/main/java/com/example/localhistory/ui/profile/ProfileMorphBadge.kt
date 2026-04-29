package com.example.localhistory.ui.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.example.localhistory.model.response.Role
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// A morphing badge that changes shape and icon based on the user's role ( student or teacher )
@Composable
fun ProfileMorphBadge(role: Role) {
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "profileShapeMorph"
    )

    LaunchedEffect(role) {
        progress = 0f
        progress = 1f
    }

    val fromShape = RoundedPolygon.Companion.circle(numVertices = 8)

    val toShape = when (role) {
        Role.STUDENT -> RoundedPolygon.Companion.star(
            numVerticesPerRadius = 6,
            innerRadius = 0.75f,
            rounding = CornerRounding(0.32f)
        )

        Role.TEACHER -> RoundedPolygon(
            numVertices = 6,
            rounding = CornerRounding(0.22f)
        )
    }

    val shape = morphShape(fromShape, toShape, animatedProgress)

    Box(
        modifier = Modifier.Companion
            .size(150.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Companion.Center
    ) {
        Icon(
            imageVector = when (role) {
                Role.STUDENT -> Icons.Outlined.Person
                Role.TEACHER -> Icons.Outlined.School
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.Companion.size(76.dp)
        )
    }
}

// Helper to generate the shape
private fun morphShape(
    from: RoundedPolygon,
    to: RoundedPolygon,
    progress: Float
): Shape {
    val morph = Morph(from, to)

    return object : Shape {
        override fun createOutline(
            size: androidx.compose.ui.geometry.Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val matrix = Matrix().apply {
                scale(size.width / 2f, size.height / 2f)
                translate(1f, 1f)
            }

            val path = morph.toPath(progress).asComposePath()
            path.transform(matrix)

            return Outline.Generic(path)
        }
    }
}
