package app.lade.draft.internal.store

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.Result
import app.lade.agenda.api.agenda.AgendaSaveModel
import app.lade.draft.internal.bar.BarStateHolder
import app.lade.draft.internal.toDraft
import app.lade.draft.internal.toEntryModel
import app.lade.draft.internal.toGoalModel
import app.lade.draftdata.DraftAlarm
import app.lade.draftdata.DraftEvent
import app.lade.draftdata.DraftGoal
import app.lade.draftdata.DraftModel
import app.lade.draftdata.DraftReminder
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

@Singleton
internal class DraftStore @Inject constructor(
    private val agendaApi: AgendaApi,
    private val kindResolver: EntryKindResolver,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val barState = BarStateHolder()

    private val _draft = MutableStateFlow(DraftModel())
    val draft: StateFlow<DraftModel> = _draft.asStateFlow()

    private val _hasChanges = MutableStateFlow(false)
    val hasChanges: StateFlow<Boolean> = _hasChanges.asStateFlow()

    private val _events = MutableSharedFlow<DraftEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<DraftEvent> = _events.asSharedFlow()

    private var originalDraft: DraftModel? = null
    private var pendingDraft: DraftModel? = null
    private var defaultDate: LocalDate? = null

    init {
        scope.launch {
            _draft.collect { draft ->
                _hasChanges.value = originalDraft != null && draft != originalDraft
            }
        }
    }

    fun setDefaultDate(date: LocalDate?) {
        defaultDate = date
    }

    fun open(entryId: Long?) {
        scope.launch {
            if (entryId != null) {
                val agenda = agendaApi.get(entryId, null)
                if (agenda != null) {
                    val loaded = agenda.toDraft()
                    originalDraft = loaded
                    _draft.value = loaded
                    recomputeKind()
                }
            } else {
                originalDraft = null
                _draft.value = DraftModel()
            }
            _events.emit(DraftEvent.OpenRequested)
        }
    }

    fun reset() {
        originalDraft = null
        pendingDraft = null
        _draft.value = DraftModel()
    }

    fun stashAndReset() {
        pendingDraft = _draft.value
        _draft.value = DraftModel()
        // originalDraft не трогаем — hasChanges останется true, когда восстановим
    }

    fun restorePending() {
        val pending = pendingDraft ?: return
        _draft.value = pending
        pendingDraft = null
        recomputeKind()
    }

    fun clearPending() {
        pendingDraft = null
    }

    fun updateTitle(title: String) {
        val current = _draft.value
        val patched = if (current.isEmpty && title.isNotBlank() && current.dateFrom == null) {
            current.copy(dateFrom = defaultDate, title = title)
        } else {
            current.copy(title = title)
        }
        _draft.value = patched
        recomputeKind()
    }

    fun updateDate(dateFrom: LocalDate?, dateTo: LocalDate?) {
        _draft.value = _draft.value.copy(dateFrom = dateFrom, dateTo = dateTo)
        recomputeKind()
    }

    fun updateTime(timeFrom: LocalTime?, timeEnd: LocalTime?) {
        _draft.value = _draft.value.copy(timeFrom = timeFrom, timeEnd = timeEnd)
        recomputeKind()
    }

    fun updateRecurrence(rrule: String?) {
        _draft.value = _draft.value.copy(rrule = rrule)
        recomputeKind()
    }

    fun updateGoals(goals: List<DraftGoal>) {
        _draft.value = _draft.value.copy(goals = goals)
        recomputeKind()
    }

    fun updateAlarms(alarms: List<DraftAlarm>) {
        _draft.value = _draft.value.copy(alarms = alarms)
        recomputeKind()
    }

    fun updateReminders(reminders: List<DraftReminder>) {
        _draft.value = _draft.value.copy(reminders = reminders)
        recomputeKind()
    }

    fun update(transform: (DraftModel) -> DraftModel) {
        _draft.value = transform(_draft.value)
        recomputeKind()
    }

    fun save() {
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
                    originalDraft = null
                    pendingDraft = null
                    _draft.value = DraftModel()
                    _events.emit(DraftEvent.Saved(result.value))
                }
                is Result.Failure -> {
                    _events.emit(DraftEvent.Error(result.error))
                }
            }
        }
    }

    fun cancel() {
        val original = originalDraft
        if (original != null) {
            _draft.value = original
            recomputeKind()
        } else {
            _draft.value = DraftModel()
        }
        scope.launch { _events.emit(DraftEvent.Cancelled) }
    }

    private fun recomputeKind() {
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
    }
}