package app.lade.draft.internal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lade.draft.DraftGoal
import app.lade.draft.internal.bar.goal.GoalCard

@Composable
internal fun DraftEditorGoalCard(
    goals: List<DraftGoal>,
    onAdd: () -> Unit,
    onGoalChange: (Int, DraftGoal) -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Цели", modifier = Modifier.weight(1f))
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                }
            }
            goals.forEachIndexed { index, goal ->
                GoalCard(
                    goal = goal,
                    onChange = { onGoalChange(index, it) },
                    onRemove = { onRemove(index) },
                )
            }
        }
    }
}