package app.lade.agendaui.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.ui.theme.Spacing
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaDetailScreen(
    entryId: Long,
    date: LocalDate,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgendaDetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(entryId, date) {
        viewModel.load(entryId, date)
    }

    val agenda by viewModel.agenda.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val goalCards by viewModel.goalCards.collectAsStateWithLifecycle()
    val periodStats by viewModel.periodStats.collectAsStateWithLifecycle()
    val markStates by viewModel.markStates.collectAsStateWithLifecycle()
    val headerState by viewModel.headerState.collectAsStateWithLifecycle()

    var showMarkSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Text(
                    text = agenda?.entry?.title ?: "Загрузка…",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
        }

        headerState?.let { EntryHeader(state = it) }

        if (goalCards.isNotEmpty()) {
            GoalGrid(goals = goalCards)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Button(
                onClick = viewModel::markQuickDone,
                modifier = Modifier.weight(1f),
            ) {
                Text("Готово")
            }
            OutlinedButton(
                onClick = viewModel::markSkip,
                modifier = Modifier.weight(1f),
            ) {
                Text("Пропустить")
            }
            OutlinedButton(
                onClick = { showMarkSheet = true },
                modifier = Modifier.weight(1f),
            ) {
                Text("Отметить")
            }
        }

        PeriodChips(
            selected = state.period,
            onSelect = viewModel::selectPeriod,
        )

        StatsRow(stats = periodStats)
        MiniChart(days = periodStats.days)
    }

    if (showMarkSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMarkSheet = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            AgendaMarkSheet(
                states = markStates,
                onUpdate = viewModel::updateMark,
                onMarkAllDone = viewModel::markAllDone,
                onSave = {
                    viewModel.saveMarks()
                    showMarkSheet = false
                },
                onDismiss = { showMarkSheet = false },
            )
        }
    }
}