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
import app.lade.calendar.api.config.TimerConfig
import app.lade.calendar.internal.list.card.expandable.CardExpandable
import app.lade.calendar.internal.list.card.expandable.CardExpandableDetails
import app.lade.calendar.internal.list.card.expandable.CardExpandableKey
import app.lade.calendar.internal.list.card.expandable.CardExpandableSummary
import app.lade.calendar.internal.list.card.expandable.rememberCardExpandableState
import app.lade.calendar.internal.list.card.timer.CardTimer
import app.lade.calendardata.api.CalendarCardModel
import app.lade.calendardata.api.CalendarGoalExpandedModel
import app.lade.entry.ui.color
import java.time.LocalDate

@Composable
internal fun ListContent(
    entries: List<CalendarCardModel>,
    topOffsetProvider: () -> Dp,
    config: ListConfig,
    timerConfig: TimerConfig,
    panelHeight: Dp,
    listState: LazyListState,
    goalDetailsCache: Map<String, List<CalendarGoalExpandedModel>>,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    onGoalValueChange: (Long, LocalDate, Long, Int) -> Unit,
    onLoadGoalDetails: (Long, LocalDate) -> Unit,
    onStartTimer: (Long, LocalDate) -> Unit,
    onFinishTimer: (Long, LocalDate, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandableState = rememberCardExpandableState()

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
                val expandableKey = CardExpandableKey.of(
                    entryId = card.entryId,
                    epochDay = card.date.toEpochDay(),
                )
                val isExpanded = expandableState.isExpanded(expandableKey)
                val details = goalDetailsCache[expandableKey.asString]

                val containerColor by animateColorAsState(
                    targetValue = if (card.allGoalsDone) {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                    label = "agendaCardContainerColor",
                )

                val timer = card.timer
                if (timer != null) {
                    CardTimer(
                        card = card,
                        timer = timer,
                        containerColor = containerColor,
                        timerConfig = timerConfig,
                        onStart = { onStartTimer(card.entryId, card.date) },
                        onFinish = { actualMinutes ->
                            onFinishTimer(card.entryId, card.date, actualMinutes)
                        },
                        modifier = Modifier,
                    )
                } else {
                    CardExpandable(
                        cornerRadius = config.entryCornerRadius,
                        stripeColor = card.entryKind.color(),
                        stripeWidth = config.entryStripeWidth,
                        containerColor = containerColor,
                        expanded = isExpanded,
                        modifier = Modifier,
                        summary = {
                            CardExpandableSummary(
                                card = card,
                                onOpenAgenda = { onOpenAgenda(card) },
                                onToggleExpand = {
                                    val willExpand = !isExpanded
                                    expandableState.toggle(expandableKey)
                                    if (willExpand) {
                                        onLoadGoalDetails(card.entryId, card.date)
                                    }
                                },
                                onLongPress = { onEntryLongPress(card) },
                            )
                        },
                        details = {
                            CardExpandableDetails(
                                details = details,
                                onBinaryClick = { goalId ->
                                    onGoalToggle(card.entryId, card.date, goalId)
                                },
                                onValueChange = { goalId, value ->
                                    onGoalValueChange(card.entryId, card.date, goalId, value)
                                },
                            )
                        },
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