package app.lade.draft.internal.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.internal.bar.DraftBarKindBadgeIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftEditorScreenInternal(
    onBack: () -> Unit,
    viewModel: DraftEditorViewModel = hiltViewModel(),
) {
    val draft by viewModel.draft.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
        ),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (draft.kind != null) {
                            DraftBarKindBadgeIcon(
                                kind = draft.kind,
                                size = 20.dp,
                            )
                        }
                        Text(
                            text = if (draft.entryId != null) "Редактирование" else "Новая запись",
                        )
                    }

                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onSave()
                            onBack()
                        },
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Сохранить",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DraftEditorTitleField(
                title = draft.title,
                onTitleChange = viewModel::onTitleChange,
            )

            DraftEditorTemporalCard(
                draft = draft,
                onDateChange = viewModel::onDateChange,
                onTimeChange = viewModel::onTimeChange,
                onRecurrenceChange = viewModel::onRecurrenceChange,
            )

            DraftEditorGoalCard(
                goals = draft.goals,
                onAdd = viewModel::onAddGoal,
                onGoalChange = viewModel::onGoalChange,
                onRemove = viewModel::onRemoveGoal,
            )

            DraftEditorReminderCard(
                reminders = draft.reminders,
                alarms = draft.alarms,
                onRemindersChange = viewModel::onRemindersChange,
                onAlarmsChange = viewModel::onAlarmsChange,
            )
        }
    }
}