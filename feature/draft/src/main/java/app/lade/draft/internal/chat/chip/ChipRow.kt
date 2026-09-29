package app.lade.draft.internal.chat.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.lade.draft.internal.chat.chip.data.ChipEmphasis
import app.lade.draft.internal.chat.chip.data.ChipModel
import app.lade.draft.internal.chat.chip.ui.ChipLeadingIcon
import app.lade.draft.internal.chat.chip.ui.ChipText
import app.lade.draft.internal.chat.chip.ui.ChipTrailingIcon
import app.lade.draft.internal.chat.chip.ui.chipColors
import app.lade.ui.theme.Spacing

@Composable
internal fun ChipRow(
    chip: ChipModel,
    modifier: Modifier = Modifier,
) {
    val active = chip.emphasis == ChipEmphasis.ACTIVE
    val colors = chipColors(chip.emphasis)
    val shape = RoundedCornerShape(50)

    val hasRemove = chip.onRemove != null

    Surface(
        shape = shape,
        color = colors.container,
        contentColor = colors.content,
        border = BorderStroke(1.dp, colors.border),
        modifier = modifier
            .clip(shape)
            .clickable(
                enabled = active,
                onClick = chip.onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(
                start = Spacing.sm,
                end = if (hasRemove) Spacing.sm else Spacing.md,
                top = Spacing.xs,
                bottom = Spacing.xs,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ChipLeadingIcon(
                icon = chip.icon,
                tint = colors.content,
            )
            ChipText(
                value = chip.value,
                color = colors.content,
            )
            if (hasRemove) {
                ChipTrailingIcon(
                    tint = colors.content,
                    onRemove = chip.onRemove,
                )
            }
        }
    }
}