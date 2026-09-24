package app.lade.entrydetailsscreen.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.entrydetailsscreen.R
import app.lade.entrydetailsscreen.domain.model.EntryEditUiState
import app.lade.entrydetailsscreen.domain.model.GoalDraft
import app.lade.entrykind.EntryKind
import app.lade.schedule.ui.AlarmModeOption
import app.lade.recurrence.api.RecurrenceDraft
import app.lade.recurrence.api.RecurrencePreset
import java.time.LocalDate
import java.time.LocalTime
import app.lade.resources.R as resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrySettingsSheet(
    state: EntryEditUiState,
    onDismiss: () -> Unit,
    onDateChange: (LocalDate?) -> Unit,
    onDateToChange: (LocalDate?) -> Unit,
    onTimeChange: (LocalTime?) -> Unit,
    onTimeEndChange: (LocalTime?) -> Unit,
    onRecurrenceChange: (RecurrenceDraft) -> Unit,
    onAlarmChange: (AlarmModeOption) -> Unit,
    onReminderChange: (Int?) -> Unit,
    onGoalChange: (Int, GoalDraft) -> Unit,
    onAddGoal: () -> Unit,
    onRemoveGoal: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                bottom = dimensionResource(resources.dimen.spacing_xl),
            ),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(resources.dimen.spacing_sm)),
        ) {
            // ── Когда ──
            item {
                SettingsSection(title = stringResource(R.string.entry_settings_section_when)) {
                    Column {
                        DateSettingsRow(
                            date = state.temporal.dateFrom,
                            onDateChange = onDateChange,
                        )
                        TimeSettingsRow(
                            label = stringResource(R.string.entry_settings_time_start),
                            time = state.temporal.time,
                            onTimeChange = onTimeChange,
                        )
                        if (state.temporal.time != null) {
                            TimeSettingsRow(
                                label = stringResource(R.string.entry_settings_time_end),
                                time = state.temporal.timeEnd,
                                onTimeChange = onTimeEndChange,
                            )
                        }
                    }
                }
            }

            // ── Повтор ──
            item {
                SettingsSection(title = stringResource(R.string.entry_settings_section_recurrence)) {
                    RecurrenceSettingsRow(
                        draft = state.temporal.recurrence,
                        presets = RecurrencePreset.HABIT,
                        onDraftChange = onRecurrenceChange,
                    )
                }
            }

            // ── Уведомление ──
            item {
                SettingsSection(title = stringResource(R.string.entry_settings_section_alarm)) {
                    Column {
                        AlarmSettingsRow(
                            mode = state.temporal.alarmMode,
                            onModeChange = onAlarmChange,
                        )
                    }
                }
            }

            // ── Цели (только для HABIT) ──
            if (state.effectiveKind == EntryKind.HABIT) {
                item {
                    SettingsSection(title = stringResource(R.string.entry_settings_section_goals)) {
                        GoalsSettingsSection(
                            goals = state.goals,
                            onGoalChange = onGoalChange,
                            onAddGoal = onAddGoal,
                            onRemoveGoal = onRemoveGoal,
                        )
                    }
                }
            }
        }
    }
}