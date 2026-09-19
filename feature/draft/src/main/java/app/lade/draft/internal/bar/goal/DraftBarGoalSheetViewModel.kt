package app.lade.draft.internal.bar.goal

import androidx.lifecycle.ViewModel
import app.lade.draft.DraftGoal
import app.lade.draft.internal.DraftApiImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class DraftBarGoalSheetViewModel @Inject constructor(
    private val api: DraftApiImpl,
) : ViewModel() {

    private val _goals = MutableStateFlow(api.draft.value.goals)
    val goals: StateFlow<List<DraftGoal>> = _goals.asStateFlow()

    fun onAdd() {
        _goals.update { it + DraftGoal() }
    }

    fun onRemove(index: Int) {
        _goals.update { list ->
            list.toMutableList().also { it.removeAt(index) }
        }
    }

    fun onGoalChange(index: Int, goal: DraftGoal) {
        _goals.update { list ->
            list.toMutableList().also { it[index] = goal }
        }
    }

    fun onApply() {
        api.updateGoals(_goals.value.filter { it.isValid })
    }
}