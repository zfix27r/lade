package app.lade.calendar.ui.component.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarListStripMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseConnection
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseStrip
import app.lade.calendar.ui.component.list.collapse.rememberCalendarCollapseMetrics
import app.lade.calendar.ui.component.list.components.AgendaCard
import app.lade.calendar.ui.component.list.components.AgendaRow
import app.lade.calendar.ui.component.list.components.CalendarListEmptyState
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.calendar.ui.component.swipe.CalendarListStripSwipe
import app.lade.calendar.ui.component.swipe.SwipeAxis
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.ui.color
import app.lade.ui.gesture.rememberSnapToEdge
import java.time.LocalDate

@Composable
fun CalendarListLayout(
    state: CalendarStateModel,
    onSwipe: (CalendarDateMode) -> Unit,
    onStripModeChange: (CalendarListStripMode) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onToggleDone: (entryId: Long, date: LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    modifier: Modifier = Modifier,
    config: CalendarListConfig = DefaultCalendarListConfig,
) {
    val listState = rememberLazyListState()
    val collapseProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val fullScrollPx = with(density) {
        (config.collapse.rowHeight * config.collapse.fullScrollRows).toPx()
    }

    val snapToEdge = rememberSnapToEdge(collapseProgress, config.collapse.snapToEdge)

    val connection = remember(collapseProgress, listState, snapToEdge, scope, fullScrollPx) {
        CalendarCollapseConnection(
            progress = collapseProgress,
            listState = listState,
            snapToEdge = snapToEdge,
            scope = scope,
            fullScrollPx = fullScrollPx,
        )
    }

    val metrics = rememberCalendarCollapseMetrics(
        currentDate = state.currentDate,
        config = config.collapse,
        fullScrollPx = fullScrollPx,
    )
    val progressValue by collapseProgress.asState()
    val listTopOffset = metrics.listTopOffset(progressValue)
    val panelHeight = config.listBottomPaddingForInputBar
    val showEmptyState = state.entries.isEmpty() && config.showEmptyState

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(connection),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CalendarDateSwipe(
                onSwipe = onSwipe,
                axis = SwipeAxis.HORIZONTAL,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CalendarListStripSwipe(
                    mode = state.stripMode,
                    onHorizontal = onSwipe,
                    onVertical = onStripModeChange,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CalendarCollapseStrip(
                        currentDate = state.currentDate,
                        entries = state.entries,
                        onDateSelected = onDateSelected,
                        progress = collapseProgress,
                        metrics = metrics,
                        config = config.collapse,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = listTopOffset + config.listTopPaddingWhenCollapsed)
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                if (showEmptyState) {
                    CalendarListEmptyState(
                        icon = Icons.Default.EventAvailable,
                        modifier = Modifier.padding(bottom = panelHeight),
                    )
                } else {
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
                            items = state.entries,
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
                                modifier = if (config.enableEntryAnimations) {
                                    Modifier.animateItem()
                                } else {
                                    Modifier
                                },
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
        }
    }
}