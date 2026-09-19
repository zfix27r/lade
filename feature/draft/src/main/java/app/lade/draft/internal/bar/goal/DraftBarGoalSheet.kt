package app.lade.draft.internal.bar.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.agenda.api.goal.GoalUnit
import app.lade.draft.DraftGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftBarGoalSheet(
    onDismiss: () -> Unit,
    viewModel: DraftBarGoalSheetViewModel = hiltViewModel(),
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Цели")

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(goals) { index, goal ->
                    GoalCard(
                        goal = goal,
                        onChange = { viewModel.onGoalChange(index, it) },
                        onRemove = { viewModel.onRemove(index) },
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = viewModel::onAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
                IconButton(onClick = {
                    viewModel.onApply()
                    onDismiss()
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Применить")
                }
            }
        }
    }
}

@Composable
fun GoalCard(
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
                Text(goal.unit.label())
            }
            DropdownMenu(
                expanded = unitExpanded,
                onDismissRequest = { unitExpanded = false },
            ) {
                GoalUnit.entries.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.label()) },
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = goal.repeat?.toString() ?: "",
                onValueChange = { onChange(goal.copy(repeat = it.toIntOrNull())) },
                label = { Text("Повтор") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = goal.weight?.toString() ?: "",
                onValueChange = { onChange(goal.copy(weight = it.toDoubleOrNull())) },
                label = { Text("Вес") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun GoalUnit.label(): String = when (this) {
    GoalUnit.LAP -> "Круги"
    GoalUnit.M -> "Метры"
    GoalUnit.KM -> "Км"
    GoalUnit.KG -> "Кг"
    GoalUnit.MIN -> "Минуты"
    GoalUnit.HOUR -> "Часы"
    GoalUnit.SET -> "Подходы"
    GoalUnit.UNKNOWN -> "—"
}