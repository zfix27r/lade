package app.lade.entrydetailsscreen.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.entrydetailsscreen.domain.model.GoalDraft
import app.lade.resources.R
import app.lade.ui.components.layout.EditSectionCard

@Composable
fun GoalSection(
    goals: List<GoalDraft>,
    onGoalChange: (Int, GoalDraft) -> Unit,
    onAddGoal: () -> Unit,
    onRemoveGoal: (Int) -> Unit,
    enabled: Boolean,
) {
    EditSectionCard(title = stringResource(R.string.edit_section_goal)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        ) {
            goals.forEachIndexed { index, draft ->
                GoalRow(
                    draft = draft,
                    onChange = { onGoalChange(index, it) },
                    onRemove = { onRemoveGoal(index) },
                )
            }
            OutlinedButton(
                onClick = onAddGoal,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            ) {
                Text(stringResource(R.string.goal_add))
            }
        }
    }
}