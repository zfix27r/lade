package app.lade.draft.internal

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.draft.DraftAlarm
import app.lade.draft.DraftApi
import app.lade.draft.DraftEvent
import app.lade.draft.DraftGoal
import app.lade.draft.DraftMode
import app.lade.draft.DraftModel
import app.lade.draft.DraftReminder
import app.lade.entrykind.EntryKindInput
import app.lade.entrykind.EntryKindResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton


internal class DraftApiImpl @Inject constructor(
    private val agendaApi: AgendaApi,
    private val kindResolver: EntryKindResolver,
) : DraftApi {
    var defaultDate: LocalDate? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _draft = MutableStateFlow(DraftModel())
    internal val draft: StateFlow<DraftModel> = _draft.asStateFlow()

    private val _mode = MutableStateFlow(DraftMode.IDLE)
    override val mode: StateFlow<DraftMode> = _mode.asStateFlow()

    private val _events = MutableSharedFlow<DraftEvent>(extraBufferCapacity = 16)
    override val events: SharedFlow<DraftEvent> = _events.asSharedFlow()

    override fun openEditor(entryId: Long?) {
        scope.launch {
            if (entryId != null) {
                val agenda = agendaApi.get(entryId, null)
                if (agenda != null) {
                    _draft.value = agenda.toDraft()
                }
            } else {
                _draft.value = DraftModel()
            }
            _events.emit(DraftEvent.OpenEditor(entryId))
        }
    }

    override fun reset() {
        _draft.value = DraftModel()
        _mode.value = DraftMode.IDLE
    }

    internal fun updateTitle(title: String) {
        val current = _draft.value
        val patched = if (current.isEmpty && title.isNotBlank() && current.dateFrom == null) {
            current.copy(dateFrom = defaultDate, title = title)
        } else {
            current.copy(title = title)
        }
        _draft.value = patched
        recomputeKindAndMode()
    }

    internal fun updateDate(dateFrom: LocalDate?, dateTo: LocalDate?) {
        _draft.value = _draft.value.copy(dateFrom = dateFrom, dateTo = dateTo)
        recomputeKindAndMode()
    }

    internal fun updateTime(timeFrom: LocalTime?, timeEnd: LocalTime?) {
        _draft.value = _draft.value.copy(timeFrom = timeFrom, timeEnd = timeEnd)
        recomputeKindAndMode()
    }

    internal fun updateRecurrence(rrule: String?) {
        _draft.value = _draft.value.copy(rrule = rrule)
        recomputeKindAndMode()
    }

    internal fun updateGoals(goals: List<DraftGoal>) {
        _draft.value = _draft.value.copy(goals = goals)
        recomputeKindAndMode()
    }

    internal fun updateAlarms(alarms: List<DraftAlarm>) {
        _draft.value = _draft.value.copy(alarms = alarms)
        recomputeKindAndMode()
    }

    internal fun updateReminders(reminders: List<DraftReminder>) {
        _draft.value = _draft.value.copy(reminders = reminders)
        recomputeKindAndMode()
    }

    private fun recomputeKindAndMode() {
        val current = _draft.value
        val kind = kindResolver.resolve(
            EntryKindInput(
                dateFrom = current.dateFrom,
                dateTo = current.dateTo,
                timeFrom = current.timeFrom,
                timeEnd = current.timeEnd,
                rrule = current.rrule,
                hasGoals = current.goals.isNotEmpty(),
            ),
        )
        _draft.value = current.copy(kind = kind)
        _mode.value = if (current.isEmpty) DraftMode.IDLE else DraftMode.ACTIVE
    }

    internal fun save() {
        val current = _draft.value
        if (current.title.isBlank()) return

        scope.launch {
            val existing = current.entryId?.let { agendaApi.get(it, null)?.entry }
            val entry = current.toEntryModel(existing)
            val saveModel = AgendaSaveModel(
                entry = entry,
                goals = current.goals.map { it.toGoalModel(entry.id) },
            )

            when (val result = agendaApi.saveAgenda(saveModel)) {
                is Result.Success -> {
                    _draft.value = DraftModel()
                    _mode.value = DraftMode.IDLE
                    _events.emit(DraftEvent.Saved(result.value))
                }
                is Result.Failure -> {
                    _events.emit(DraftEvent.Error(result.error))
                }
            }
        }
    }

    internal fun cancel() {
        _draft.value = DraftModel()
        _mode.value = DraftMode.IDLE
        scope.launch { _events.emit(DraftEvent.Cancelled) }
    }
}