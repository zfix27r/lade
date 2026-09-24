package app.lade.agendaui.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.agenda.api.AgendaApi
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.agenda.api.log.LogOrigin
import app.lade.agenda.api.log.LogSaveGoalModel
import app.lade.agenda.api.log.LogSaveModel
import app.lade.humanize.api.Humanize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class AgendaStatsPeriod {
    WEEK,
    MONTH,
    ALL,
}

data class AgendaDetailState(
    val period: AgendaStatsPeriod = AgendaStatsPeriod.WEEK,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AgendaDetailViewModel @Inject constructor(
    private val agendaApi: AgendaApi,
    private val humanize: Humanize,
) : ViewModel() {

    private val _agenda = MutableStateFlow<AgendaModel?>(null)
    val agenda: StateFlow<AgendaModel?> = _agenda.asStateFlow()

    private val _state = MutableStateFlow(AgendaDetailState())
    val state: StateFlow<AgendaDetailState> = _state.asStateFlow()

    private val entryId = MutableStateFlow<Long?>(null)
    private val anchorDate = MutableStateFlow<LocalDate?>(null)

    private val _markStates = MutableStateFlow<List<GoalMarkState>>(emptyList())
    val markStates: StateFlow<List<GoalMarkState>> = _markStates.asStateFlow()

    init {
        viewModelScope.launch {
            agenda.collect { model ->
                if (model != null) {
                    _markStates.value = buildMarkStates(
                        agenda = model,
                        history = periodEntries.value,
                        humanize = humanize,
                    )
                } else {
                    _markStates.value = emptyList()
                }
            }
        }
    }

    val headerState: StateFlow<HeaderState?> = combine(agenda, anchorDate) { model, anchor ->
        if (model == null || anchor == null) null
        else model.entry.toHeaderState(humanize, anchor)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun updateMark(goalId: Long, value: Int) {
        _markStates.value = _markStates.value.map { state ->
            if (state.goalId == goalId) {
                state.copy(currentTotal = value.coerceIn(0, state.targetTotal))
            } else state
        }
    }

    fun markAllDone() {
        _markStates.value = _markStates.value.map { it.copy(currentTotal = it.targetTotal) }
    }

    fun saveMarks() {
        val model = _agenda.value ?: return
        val goals = _markStates.value.map { state ->
            val (a, r) = splitFact(state.currentTotal, state.amount, state.repeat)
            LogSaveGoalModel(
                goalId = state.goalId,
                amount = a,
                repeat = r,
                weight = state.weight,
            )
        }
        save(
            LogSaveModel(
                date = model.date,
                goals = goals,
                origin = LogOrigin.AGENDA,
            )
        )
    }

    fun markQuickDone() {
        val model = _agenda.value ?: return
        val goals = model.goals.map { goal ->
            LogSaveGoalModel(
                goalId = goal.id,
                amount = goal.amount,
                repeat = goal.repeat,
                weight = goal.weight,
            )
        }
        save(
            LogSaveModel(
                date = model.date,
                goals = goals,
                origin = LogOrigin.AGENDA,
            )
        )
    }

    fun markSkip() {
        val model = _agenda.value ?: return
        val goals = model.goals.map { goal ->
            LogSaveGoalModel(
                goalId = goal.id,
                amount = 0,
                repeat = 0,
                weight = goal.weight,
            )
        }
        save(
            LogSaveModel(
                date = model.date,
                goals = goals,
                origin = LogOrigin.AGENDA,
            )
        )
    }

    private fun save(model: LogSaveModel) {
        viewModelScope.launch {
            agendaApi.saveLogs(model)
            val id = _agenda.value?.entry?.id ?: return@launch
            _agenda.value = agendaApi.get(id, model.date)
        }
    }

    val periodEntries: StateFlow<List<AgendaModel>> = combine(
        _state,
        entryId,
        anchorDate,
    ) { st, id, anchor ->
        Triple(st.period, id, anchor)
    }
        .flatMapLatest { (period, id, anchor) ->
            if (id == null || anchor == null) {
                flowOf(emptyList())
            } else {
                val from = when (period) {
                    AgendaStatsPeriod.WEEK -> anchor.minusDays(6)
                    AgendaStatsPeriod.MONTH -> anchor.minusDays(29)
                    AgendaStatsPeriod.ALL -> anchor.minusYears(5)
                }
                agendaApi.observeRange(from, anchor)
                    .map { list -> list.filter { it.entry.id == id } }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val periodStats: StateFlow<PeriodStats> = periodEntries
        .map { entries -> buildPeriodStats(entries, LocalDate.now()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PeriodStats(emptyList(), 0, 0, 0, 0, 0),
        )

    val goalCards: StateFlow<List<GoalCardState>> = agenda
        .map { model -> model?.let { buildGoalCards(it, humanize) } ?: emptyList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun load(id: Long, date: LocalDate) {
        entryId.value = id
        anchorDate.value = date
        viewModelScope.launch {
            _agenda.value = agendaApi.get(id, date)
        }
    }

    fun selectPeriod(period: AgendaStatsPeriod) {
        _state.value = _state.value.copy(period = period)
    }
}