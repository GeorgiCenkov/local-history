package com.example.localhistory.ui.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

// define the shapes to morph between for each page index ( used for stylistic purposes )
// one shape per page index
private fun shapeForIndex(index: Int): RoundedPolygon = when (index) {
    0    -> RoundedPolygon.circle(numVertices = 8)
    1    -> RoundedPolygon.star(
        numVerticesPerRadius = 6,
        innerRadius          = 0.75f,
        rounding             = CornerRounding(0.32f)
    )
    2    -> RoundedPolygon(
        numVertices = 4,
        rounding    = CornerRounding(0.3f)
    )
    else -> RoundedPolygon(
        numVertices = 6,
        rounding    = CornerRounding(0.2f)
    )
}

// morphs between two RoundedPolygons at a given progress
private fun morphShape(from: RoundedPolygon, to: RoundedPolygon, progress: Float): Shape {
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

@Composable
fun OnboardingPage(
    page:       OnboardingPage,
    pageIndex:  Int,
    extra:      @Composable (() -> Unit)? = null
) {
    // animate morph progress 0f → 1f when page becomes visible
    var morphProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue    = morphProgress,
        animationSpec  = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label          = "shapeMorph"
    )

    LaunchedEffect(pageIndex) {
        morphProgress = 0f
        morphProgress = 1f
    }

    val fromShape = shapeForIndex(pageIndex)
    val toShape   = shapeForIndex((pageIndex + 1) % 4)
    val shape     = morphShape(fromShape, toShape, animatedProgress)

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier         = Modifier
                .size(200.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = page.icon,
                contentDescription = null,
                modifier           = Modifier.size(96.dp),
                tint               = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text      = page.title,
            style     = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text      = page.subtitle,
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        if (extra != null) {
            Spacer(modifier = Modifier.height(32.dp))
            extra()
        }
    }
}