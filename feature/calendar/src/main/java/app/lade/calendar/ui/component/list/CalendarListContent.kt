package app.lade.calendar.ui.component.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.calendar.ui.component.list.components.AgendaCard
import app.lade.calendar.ui.component.list.components.AgendaRow
import app.lade.calendar.ui.component.swipe.SwipeResult
import app.lade.calendar.ui.component.swipe.awaitSwipe
import app.lade.calendardata.api.CalendarCardModel
import app.lade.entry.ui.color
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CalendarListContent(
    entries: List<CalendarCardModel>,
    topOffset: Dp,
    fullScrollPx: Float,
    progress: Animatable<Float, *>,
    config: CalendarListConfig,
    panelHeight: Dp,
    onOpenAgenda: (CalendarCardModel) -> Unit,
    onEntryLongPress: (CalendarCardModel) -> Unit,
    onToggleDone: (Long, LocalDate) -> Unit,
    onGoalToggle: (Long, LocalDate, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val thresholdPx = with(density) { 8.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topOffset)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(fullScrollPx, thresholdPx) {
                awaitPointerEventScope {
                    while (true) {
                        val result = awaitSwipe(
                            thresholdPx = thresholdPx,
                            onDrag = { isVertical, dy ->
                                if (isVertical) {
                                    val canScroll = listState.canScrollBackward ||
                                            listState.canScrollForward
                                    if (!canScroll) {
                                        scope.launch {
                                            val next = (progress.value - dy / fullScrollPx)
                                                .coerceIn(0f, 1f)
                                            progress.snapTo(next)
                                        }
                                    }
                                }
                            },
                        )
                        if (result is SwipeResult.Vertical) {
                            val canScroll = listState.canScrollBackward ||
                                    listState.canScrollForward
                            if (!canScroll) {
                                val target = if (progress.value > 0.5f) 1f else 0f
                                scope.launch { progress.animateTo(target, tween(200)) }
                            }
                        }
                    }
                }
            },
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