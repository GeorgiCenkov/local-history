package com.example.localhistory.ui.landmark

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.ui.components.NetworkImage

// The main view landmark card, used in a list view
@Composable
fun LandmarkCard(
    landmark: LandmarkDTO,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable(onClick = onOpen)
    ) {
        Row(
            modifier = Modifier.Companion.padding(12.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            NetworkImage(
                imageUrl = landmark.imageUrl,
                contentDescription = landmark.title,
                modifier = Modifier.Companion
                    .size(92.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.Companion.width(14.dp))
            Column(modifier = Modifier.Companion.weight(1f)) {
                Text(
                    text = landmark.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis
                )
                Spacer(Modifier.Companion.height(4.dp))
                Text(
                    text = landmark.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Companion.Ellipsis
                )
                Spacer(Modifier.Companion.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.landmark_card_stats,
                        landmark.visitRewardPoints,
                        landmark.visitsCount
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.Companion.End) {
                IconButton(onClick = onOpen) {
                    Icon(
                        Icons.Outlined.Visibility,
                        contentDescription = stringResource(R.string.action_view)
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.action_edit)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.action_delete)
                    )
                }
            }
        }
    }
}