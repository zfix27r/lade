package app.lade.draft.internal.chat.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lade.draft.internal.chat.chip.domain.ChipEmphasis
import app.lade.draft.internal.chat.chip.domain.ChipModel
import app.lade.ui.theme.IconSizes
import app.lade.ui.theme.Spacing

@Composable
internal fun ChipRow(
    chip: ChipModel,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val active = chip.emphasis == ChipEmphasis.ACTIVE
    val shape = RoundedCornerShape(50)

    val containerColor = if (active) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f)
    }
    val contentColor = if (active) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }
    val borderColor = if (active) {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    }

    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .clip(shape)
            .clickable(
                enabled = active,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(
                start = Spacing.sm,
                end = Spacing.sm,
                top = Spacing.xs,
                bottom = Spacing.xs,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                imageVector = chip.data.icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(IconSizes.sm),
            )
            Text(
                text = chip.data.value,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(IconSizes.sm)
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onRemove),
            )
        }
    }
}