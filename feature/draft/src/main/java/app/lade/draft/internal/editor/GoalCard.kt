package app.lade.draft.internal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.agenda.api.goal.GoalUnit
import app.lade.draft.DraftGoal

@Composable
internal fun GoalCard(
    goal: DraftGoal,
    onChange: (DraftGoal) -> Unit,
    onRemove: () -> Unit,
) {
    var unitExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = goal.title,
                onValueChange = { onChange(goal.copy(title = it)) },
                label = { Text("Название") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Удалить")
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { unitExpanded = true }) {
                Text(goal.unit.displayName())
            }
            DropdownMenu(
                expanded = unitExpanded,
                onDismissRequest = { unitExpanded = false },
            ) {
                GoalUnit.entries.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.displayName()) },
                        onClick = {
                            onChange(goal.copy(unit = unit))
                            unitExpanded = false
                        },
                    )
                }
            }

            OutlinedTextField(
                value = goal.amount?.toString() ?: "",
                onValueChange = { onChange(goal.copy(amount = it.toIntOrNull())) },
                label = { Text("Кол-во") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun GoalUnit.displayName(): String = when (this) {
    GoalUnit.LAP -> "Круги"
    GoalUnit.M -> "Метры"
    GoalUnit.KM -> "Км"
    GoalUnit.KG -> "Кг"
    GoalUnit.MIN -> "Минуты"
    GoalUnit.HOUR -> "Часы"
    GoalUnit.SET -> "Подходы"
    GoalUnit.UNKNOWN -> "—"
}