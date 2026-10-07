package app.lade.calendar.internal.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import app.lade.calendar.api.config.ListConfig
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.ui.color
import java.time.LocalDate

@Composable
internal fun ListContent(
    entries: List<CalendarCardModel>,
    topOffsetProvider: () -> Dp,
    config: ListConfig,
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
            .offset { IntOffset(0, topOffsetProvider().roundToPx()) }
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = config.contentPadding,
                end = config.contentPadding,
                bottom = panelHeight + config.contentBottomPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(config.entrySpacing),
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
                ListCard(
                    cornerRadius = config.entryCornerRadius,
                    stripeColor = card.entryKind.color(),
                    stripeWidth = config.entryStripeWidth,
                    containerColor = containerColor,
                    modifier = Modifier,
                ) {
                    ListRow(
                        card = card,
                        onOpenAgenda = { onOpenAgenda(card) },
                        onToggleDone = { onToggleDone(card.entryId, card.date) },
                        onGoalToggle = { goalId ->
                            onGoalToggle(card.entryId, card.date, goalId)
                        },
                        onLongPress = { onEntryLongPress(card) },
                        enableMarkHaptics = config.enableMarkHaptics,
                        goalsVisibilityLimit = config.goalsVisibilityLimit,
                    )
                }
            }
        }
        if (entries.isEmpty()) {
            CalendarListEmptyState(
                icon = Icons.AutoMirrored.Filled.EventNote,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}