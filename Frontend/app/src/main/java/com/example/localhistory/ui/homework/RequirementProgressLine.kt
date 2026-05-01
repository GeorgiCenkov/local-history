package com.example.localhistory.ui.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R

// One row inside the assignment requirement checklist.
@Composable
fun RequirementProgressLine(
    done: Boolean,
    icon: ImageVector,
    text: String
) {
    val color = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Row(verticalAlignment = Alignment.Companion.CenterVertically) {
        Box(
            contentAlignment = Alignment.Companion.Center,
            modifier = Modifier.Companion
                .size(32.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = if (done) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = color,
                modifier = Modifier.Companion.size(18.dp)
            )
        }
        Spacer(Modifier.Companion.width(10.dp))
        Column(modifier = Modifier.Companion.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    if (done) R.string.homework_requirement_done
                    else R.string.homework_requirement_pending
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.Companion.size(18.dp)
        )
    }
}