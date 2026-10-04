package app.lade.calendar.ui.component.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import app.lade.calendar.ui.component.list.components.AgendaCard
import app.lade.calendar.ui.component.list.components.AgendaRow
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.ui.color
import java.time.LocalDate

@Composable
fun CalendarListContent(
    entries: List<CalendarCardModel>,
    topOffset: Dp,
    config: CalendarListConfig,
    panelHeight: Dp,
    listState: LazyListState,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topOffset)
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = config.listContentHorizontalPadding,
                end = config.listContentHorizontalPadding,
                bottom = panelHeight + config.listContentBottomPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(config.listEntrySpacing),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = entries,
                key = { "entry-${it.entryId}-${it.date.toEpochDay()}" },
            ) { card ->
                val containerColor by animateColorAsState(
                    targetValue = if (card.allGoalsDone) {
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    },
                    label = "agendaCardContainerColor",
                )
                AgendaCard(
                    cornerRadius = config.listEntryCornerRadius,
                    stripeColor = card.entryKind.color(),
                    stripeWidth = config.listEntryTypeStripeWidth,
                    containerColor = containerColor,
                    modifier = Modifier,
                ) {
                    AgendaRow(
                        card = card,
                        onOpenAgenda = { onOpenAgenda(card) },
                        onToggleDone = { onToggleDone(card.entryId, card.date) },
                        onGoalToggle = { goalId ->
                            onGoalToggle(card.entryId, card.date, goalId)
                        },
                        onLongPress = { onEntryLongPress(card) },
                        enableMarkHaptics = config.enableMarkHaptics,
                    )
                }
            }
        }
    }
}