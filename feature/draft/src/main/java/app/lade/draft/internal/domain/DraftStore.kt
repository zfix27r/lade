package app.lade.draft.internal.domain

import app.lade.agenda.api.Result
import app.lade.draft.internal.data.DraftRepository
import app.lade.draftdata.DraftAlarm
import app.lade.draftdata.DraftGoal
import app.lade.draftdata.DraftModel
import app.lade.draftdata.DraftReminder
import app.lade.entrykind.EntryKindInput
import app.lade.entrykind.EntryKindResolver
import kotlinx.coroutines.CoroutineScope
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

internal class DraftStore @Inject constructor(
    private val repository: DraftRepository,
    private val kindResolver: EntryKindResolver,
    private val scope: CoroutineScope,
) {

    private val _state = MutableStateFlow(DraftState())
    val state: StateFlow<DraftState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DraftEffect>(
        extraBufferCapacity = 16,
    )
    val effects: SharedFlow<DraftEffect> = _effects.asSharedFlow()

    fun dispatch(intent: DraftIntent) {
        when (intent) {
            is DraftIntent.SetDefaultDate -> setDefaultDate(intent.date)
            is DraftIntent.Open -> open(intent.entryId)
            DraftIntent.Reset -> reset()
            DraftIntent.StashAndReset -> stashAndReset()
            DraftIntent.RestorePending -> restorePending()
            DraftIntent.ClearPending -> clearPending()

            is DraftIntent.UpdateTitle -> updateTitle(intent.title)
            is DraftIntent.UpdateDate -> updateDate(intent.dateFrom, intent.dateTo)
            is DraftIntent.UpdateTime -> updateTime(intent.timeFrom, intent.timeEnd)
            is DraftIntent.UpdateRecurrence -> updateRecurrence(intent.rrule)
            is DraftIntent.UpdateGoals -> updateGoals(intent.goals)
            is DraftIntent.UpdateAlarms -> updateAlarms(intent.alarms)
            is DraftIntent.UpdateReminders -> updateReminders(intent.reminders)
            is DraftIntent.Update -> update(intent.transform)

            DraftIntent.Save -> save()
            DraftIntent.Cancel -> cancel()
        }
    }

    private fun setDefaultDate(date: LocalDate?) {
        mutate { it.copy(defaultDate = date) }
    }

    private fun open(entryId: Long?) {
        scope.launch {
            val loaded = if (entryId != null) repository.load(entryId) else null
            if (loaded != null) {
                mutate {
                    it.copy(
                        draft = loaded,
                        original = loaded,
                        pending = null,
                    )
                }
            } else {
                mutate {
                    it.copy(
                        draft = DraftModel(),
                        original = null,
                    )
                }
            }
            _effects.emit(DraftEffect.OpenRequested(_state.value.draft))
        }
    }

    private fun reset() {
        mutate {
            it.copy(
                draft = DraftModel(),
                original = null,
                pending = null,
            )
        }
    }

    private fun stashAndReset() {
        mutate {
            it.copy(
                pending = it.draft,
                draft = DraftModel(),
            )
        }
    }

    private fun restorePending() {
        val pending = _state.value.pending ?: return
        mutate {
            it.copy(
                draft = pending,
                pending = null,
            )
        }
    }

    private fun clearPending() {
        mutate { it.copy(pending = null) }
    }

    private fun updateTitle(title: String) {
        val current = _state.value.draft
        val patched = if (current.isEmpty && title.isNotBlank() && current.dateFrom == null) {
            current.copy(dateFrom = _state.value.defaultDate, title = title)
        } else {
            current.copy(title = title)
        }
        mutate { it.copy(draft = patched) }
    }

    private fun updateDate(dateFrom: LocalDate?, dateTo: LocalDate?) {
        mutate { it.copy(draft = it.draft.copy(dateFrom = dateFrom, dateTo = dateTo)) }
    }

    private fun updateTime(timeFrom: LocalTime?, timeEnd: LocalTime?) {
        mutate { it.copy(draft = it.draft.copy(timeFrom = timeFrom, timeEnd = timeEnd)) }
    }

    private fun updateRecurrence(rrule: String?) {
        mutate { it.copy(draft = it.draft.copy(rrule = rrule)) }
    }

    private fun updateGoals(goals: List<DraftGoal>) {
        mutate { it.copy(draft = it.draft.copy(goals = goals)) }
    }

    private fun updateAlarms(alarms: List<DraftAlarm>) {
        mutate { it.copy(draft = it.draft.copy(alarms = alarms)) }
    }

    private fun updateReminders(reminders: List<DraftReminder>) {
        mutate { it.copy(draft = it.draft.copy(reminders = reminders)) }
    }

    private fun update(transform: (DraftModel) -> DraftModel) {
        mutate { it.copy(draft = transform(it.draft)) }
    }

    private fun save() {
        val current = _state.value.draft
        if (current.title.isBlank()) return

        scope.launch {
            when (val result = repository.save(current)) {
                is Result.Success -> {
                    mutate {
                        it.copy(
                            draft = DraftModel(),
                            original = null,
                            pending = null,
                        )
                    }
                    _effects.emit(DraftEffect.Saved(result.value))
                }
                is Result.Failure -> {
                    _effects.emit(DraftEffect.Error(result.error))
                }
            }
        }
    }

    private fun cancel() {
        val original = _state.value.original
        if (original != null) {
            mutate { it.copy(draft = original) }
        } else {
            mutate { it.copy(draft = DraftModel()) }
        }
        scope.launch { _effects.emit(DraftEffect.Cancelled) }
    }

    private fun mutate(transform: (DraftState) -> DraftState) {
        val next = transform(_state.value)
        _state.value = next.copy(draft = recomputeKind(next.draft))
    }

    private fun recomputeKind(draft: DraftModel): DraftModel {
        val kind = kindResolver.resolve(
            EntryKindInput(
                dateFrom = draft.dateFrom,
                dateTo = draft.dateTo,
                timeFrom = draft.timeFrom,
                timeEnd = draft.timeEnd,
                rrule = draft.rrule,
                hasGoals = draft.goals.isNotEmpty(),
            ),
        )
        return draft.copy(kind = kind)
    }
}