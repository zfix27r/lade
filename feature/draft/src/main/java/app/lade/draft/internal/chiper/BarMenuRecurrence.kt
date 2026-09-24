package app.lade.draft.internal.chiper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import app.lade.draft.R
import app.lade.draftdata.DraftModel
import app.lade.ui.theme.Spacing

@Composable
internal fun BarMenuRecurrence(
    draft: DraftModel,
    onRecurrenceChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val current = BarRecurrencePreset.fromRrule(draft.rrule)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BarRecurrencePreset.entries.forEach { preset ->
            RecurrenceChip(
                preset = preset,
                selected = current == preset,
                onClick = { onRecurrenceChange(preset.rrule) },
            )
        }
    }
}

@Composable
private fun RecurrenceChip(
    preset: BarRecurrencePreset,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(preset.labelRes()),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
        )
    }
}

private fun BarRecurrencePreset.labelRes(): Int = when (this) {
    BarRecurrencePreset.NONE -> R.string.draft_menu_recurrence_none
    BarRecurrencePreset.DAILY -> R.string.draft_menu_recurrence_daily
    BarRecurrencePreset.WEEKDAYS -> R.string.draft_menu_recurrence_weekdays
    BarRecurrencePreset.WEEKENDS -> R.string.draft_menu_recurrence_weekends
    BarRecurrencePreset.WEEKLY -> R.string.draft_menu_recurrence_weekly
    BarRecurrencePreset.MONTHLY -> R.string.draft_menu_recurrence_monthly
    BarRecurrencePreset.YEARLY -> R.string.draft_menu_recurrence_yearly
}