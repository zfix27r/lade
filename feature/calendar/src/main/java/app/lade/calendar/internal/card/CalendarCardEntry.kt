package app.lade.calendar.internal.card

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.calendar.internal.card.expandable.CardExpandableDetails
import app.lade.calendar.internal.card.timer.CardTimerRow
import app.lade.ui.components.card.ladeCardContainerColor
import app.lade.ui.theme.Radius
import app.lade.ui.theme.Spacing
import java.time.LocalDate

@Composable
internal fun CalendarCardEntry(
    agenda: CalendarDataFullAgendaModel,
    date: LocalDate,
    expanded: Boolean,
    onOpenAgenda: () -> Unit,
    onLongPress: () -> Unit,
    onToggleExpand: () -> Unit,
    onGoalBinaryClick: (Long) -> Unit,
    onGoalValueChange: (Long, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val goals = agenda.goals
    val hasGoals = goals != null
    val isCompleted = goals?.isAllDone == true
    val containerColor = ladeCardContainerColor(isCompleted = isCompleted)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.sm),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = Spacing.none),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            CalendarCardEntrySummary(
                agenda = agenda,
                onOpenAgenda = onOpenAgenda,
                onLongPress = onLongPress,
                onToggleExpand = onToggleExpand,
            )

            AnimatedContent(
                targetState = expanded && hasGoals,
                transitionSpec = {
                    (fadeIn() + expandVertically()) togetherWith
                            (fadeOut() + shrinkVertically())
                },
                label = "CalendarCardEntryDetails",
            ) { show ->
                if (show) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        agenda.timer?.let { timer ->
                            CardTimerRow(timer = timer)
                        }
                        CardExpandableDetails(
                            goals = goals?.goals.orEmpty(),
                            onBinaryClick = onGoalBinaryClick,
                            onValueChange = onGoalValueChange,
                        )
                    }
                }
            }
        }
    }
}