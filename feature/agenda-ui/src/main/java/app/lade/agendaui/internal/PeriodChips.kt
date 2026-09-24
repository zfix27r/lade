package app.lade.agendaui.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.ui.theme.Spacing

@Composable
fun PeriodChips(
    selected: AgendaStatsPeriod,
    onSelect: (AgendaStatsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        AgendaStatsPeriod.entries.forEach { period ->
            FilterChip(
                selected = period == selected,
                onClick = { onSelect(period) },
                label = { Text(period.label()) },
            )
        }
    }
}

private fun AgendaStatsPeriod.label(): String = when (this) {
    AgendaStatsPeriod.WEEK -> "Неделя"
    AgendaStatsPeriod.MONTH -> "Месяц"
    AgendaStatsPeriod.ALL -> "Всё"
}