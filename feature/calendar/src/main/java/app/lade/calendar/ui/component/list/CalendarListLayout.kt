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
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.domain.CalendarStateModel
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseConnection
import app.lade.calendar.ui.component.list.collapse.CalendarCollapseStrip
import app.lade.calendar.ui.component.list.collapse.rememberCalendarCollapseMetrics
import app.lade.calendar.ui.component.list.components.AgendaCard
import app.lade.calendar.ui.component.list.components.AgendaRow
import app.lade.calendar.ui.component.list.components.CalendarListEmptyState
import app.lade.calendar.ui.component.list.components.entryTypeColor
import app.lade.ui.gesture.rememberSnapToEdge
import java.time.LocalDate

@Composable
fun CalendarListLayout(
    state: CalendarStateModel,
    onSwipe: (CalendarDateMode) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onOpenAgenda: (AgendaModel) -> Unit,
    onEntryLongPress: (AgendaModel) -> Unit,
    onMarkDone: (entryId: Long, date: LocalDate) -> Unit,
    onMarkSkip: (entryId: Long, date: LocalDate) -> Unit,
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
    val metrics = rememberCalendarCollapseMetrics(
        currentDate = state.currentDate,
        config = config.collapse,
        fullScrollPx = fullScrollPx,
    )
    val progressValue by collapseProgress.asState()
    val listTopOffset = metrics.listTopOffset(progressValue)

    val connection = remember(collapseProgress, listState, snapToEdge, scope, fullScrollPx) {
        CalendarCollapseConnection(
            progress = collapseProgress,
            listState = listState,
            snapToEdge = snapToEdge,
            scope = scope,
            fullScrollPx = fullScrollPx,
        )
    }

    val panelHeight = config.listBottomPaddingForInputBar
    val showEmptyState = state.entries.isEmpty() && config.showEmptyState

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(connection),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CalendarCollapseStrip(
                currentDate = state.currentDate,
                entries = state.entries,
                onDateSelected = onDateSelected,
                progress = collapseProgress,
                metrics = metrics,
                config = config.collapse,
                modifier = Modifier.fillMaxWidth(),
            )
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
                            key = { "entry-${it.entry.id}-${it.date.toEpochDay()}" },
                        ) { agenda ->
                            val done = agenda.logs.any { (it.actualAmount ?: 0) > 0 }
                            val containerColor by animateColorAsState(
                                targetValue = if (done) {
                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                } else {
                                    MaterialTheme.colorScheme.surfaceContainerLow
                                },
                                label = "agendaCardContainerColor",
                            )
                            AgendaCard(
                                cornerRadius = config.listEntryCornerRadius,
                                stripeColor = entryTypeColor(agenda.entry.kind),
                                stripeWidth = config.listEntryTypeStripeWidth,
                                containerColor = containerColor,
                                modifier = if (config.enableEntryAnimations) {
                                    Modifier.animateItem()
                                } else {
                                    Modifier
                                },
                            ) {
                                AgendaRow(
                                    agenda = agenda,
                                    onOpenAgenda = { onOpenAgenda(agenda) },
                                    onMarkDone = { onMarkDone(agenda.entry.id, agenda.date) },
                                    onMarkSkip = { onMarkSkip(agenda.entry.id, agenda.date) },
                                    onLongPress = { onEntryLongPress(agenda) },
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