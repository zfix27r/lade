package app.lade.entrydetailsscreen.ui.edit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.entrydetailsscreen.domain.model.EntryEditUiState
import app.lade.entrydetailsscreen.R
import app.lade.resources.R as resources
import app.lade.schedule.ui.AlarmModeOption
import app.lade.schedule.ui.label
import app.lade.recurrence.api.RecurrencePreset
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun EntrySummaryRow(
    state: EntryEditUiState,
    modifier: Modifier = Modifier,
) {
    val parts = buildSummaryParts(state)
    if (parts.isEmpty()) return

    Text(
        text = parts.joinToString(" · "),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(resources.dimen.screen_padding),
                vertical = dimensionResource(resources.dimen.spacing_xs),
            ),
    )
}

@Composable
private fun buildSummaryParts(state: EntryEditUiState): List<String> {
    val t = state.temporal
    val parts = mutableListOf<String>()

    t.dateFrom?.let { date ->
        parts += formatDate(date)
    }

    t.time?.let { time ->
        val end = t.timeEnd
        parts += if (end != null) {
            "${formatTime(time)}–${formatTime(end)}"
        } else {
            formatTime(time)
        }
    }

    if (t.recurrence.preset != RecurrencePreset.None) {
        parts += t.recurrence.preset.label()
    }

    if (t.alarmMode != AlarmModeOption.None) {
        parts += t.alarmMode.label()
    }

    t.reminderMinutesBefore?.let { minutes ->
        parts += stringResource(R.string.entry_summary_reminder, minutes)
    }

    return parts
}

@Composable
private fun formatDate(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> stringResource(R.string.entry_summary_today)
        today.plusDays(1) -> stringResource(R.string.entry_summary_tomorrow)
        today.minusDays(1) -> stringResource(R.string.entry_summary_yesterday)
        else -> {
            val fmt = remember {
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    .withLocale(Locale.getDefault())
            }
            date.format(fmt)
        }
    }
}

private fun formatTime(time: LocalTime): String =
    "%02d:%02d".format(time.hour, time.minute)