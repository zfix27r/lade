package app.lade.agendaui.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.ui.theme.Spacing

@Composable
internal fun AgendaMarkSheet(
    states: List<GoalMarkState>,
    onUpdate: (goalId: Long, value: Int) -> Unit,
    onMarkAllDone: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var applied by remember { mutableStateOf(false) }

    LaunchedEffect(states) {
        if (!applied && states.isNotEmpty()) {
            states.forEach { state ->
                if (state.currentTotal == 0 && state.averageTotal > 0) {
                    onUpdate(state.goalId, state.averageTotal)
                }
            }
            applied = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.lg)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = "Отметить",
            style = MaterialTheme.typography.titleLarge,
        )

        OutlinedButton(
            onClick = onMarkAllDone,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Всё выполнено")
        }

        states.forEach { state ->
            GoalMarkRow(
                state = state,
                onValueChange = { value -> onUpdate(state.goalId, value) },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            ) {
                Text("Отмена")
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun GoalMarkRow(
    state: GoalMarkState,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.title.ifBlank { "Цель" },
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = "${state.currentText} / ${state.targetText}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = state.currentTotal.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..state.targetTotal.toFloat().coerceAtLeast(1f),
            steps = (state.targetTotal - 1).coerceAtLeast(0),
        )
    }
}