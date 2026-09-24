package app.lade.draft.internal.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.chat.api.ChatApi
import app.lade.chat.api.ParseResult
import app.lade.draft.internal.chat.chip.BarChipKind
import app.lade.draft.internal.parse.toDraftModel
import app.lade.draft.internal.store.DraftStore
import app.lade.humanize.api.Humanize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
internal class DraftBarChatViewModel @Inject constructor(
    private val store: DraftStore,
    private val chatApi: ChatApi,
    humanize: Humanize,
) : ViewModel() {

    private val chipsBuilder = DraftChipsBuilder(humanize)

    val chips: StateFlow<DraftChips> = store.draft
        .map { draft ->
            chipsBuilder.build(
                model = draft,
                onChipClick = ::onChipClick,
                onChipRemove = ::onChipRemove,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DraftChips(emptyList(), emptyList()),
        )

    private val _parseResult = MutableStateFlow(ParseResult(emptyList(), emptyList(), ""))
    val parseResult: StateFlow<ParseResult> = _parseResult.asStateFlow()

    private var parseJob: Job? = null

    fun onTextChange(text: String) {
        parseJob?.cancel()
        if (text.isBlank()) {
            _parseResult.value = ParseResult(emptyList(), emptyList(), "")
            return
        }
        parseJob = viewModelScope.launch {
            delay(DEBOUNCE_MS.milliseconds)
            val current = store.draft.value
            val result = chatApi.parse(text, current)
            _parseResult.value = result
            store.update { result.toDraftModel(it) }
        }
    }

    fun onSubmit() {
        parseJob?.cancel()
        store.save()
        _parseResult.value = ParseResult(emptyList(), emptyList(), "")
    }

    fun clearParse() {
        parseJob?.cancel()
        _parseResult.value = ParseResult(emptyList(), emptyList(), "")
    }

    fun onChipClick(kind: BarChipKind, index: Int) {
        when (kind) {
            BarChipKind.DATE_FROM, BarChipKind.DATE_TO -> Unit
            BarChipKind.TIME_FROM, BarChipKind.TIME_END -> Unit
            BarChipKind.RRULE -> Unit
            BarChipKind.GOAL -> Unit
        }
    }

    fun onChipRemove(kind: BarChipKind, index: Int) {
        store.update { model ->
            when (kind) {
                BarChipKind.DATE_FROM -> model.copy(dateFrom = null, dateTo = null)
                BarChipKind.DATE_TO -> model.copy(dateTo = null)
                BarChipKind.TIME_FROM -> model.copy(timeFrom = null, timeEnd = null)
                BarChipKind.TIME_END -> model.copy(timeEnd = null)
                BarChipKind.RRULE -> model.copy(rrule = null)
                BarChipKind.GOAL -> model.copy(
                    goals = model.goals.filterIndexed { i, _ -> i != index },
                )
            }
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}