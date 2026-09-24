package app.lade.agendaui.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.ui.theme.Spacing

@Composable
internal fun StatsRow(
    stats: PeriodStats,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        StatItem(
            label = "План",
            value = stats.planned.toString(),
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = "Закрыто",
            value = stats.done.toString(),
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = "Частично",
            value = stats.partial.toString(),
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = "Пропуск",
            value = stats.skipped.toString(),
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = "Не отм.",
            value = stats.notMarked.toString(),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}