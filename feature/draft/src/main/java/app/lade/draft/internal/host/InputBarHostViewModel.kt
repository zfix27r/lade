package app.lade.draft.internal.host

import androidx.lifecycle.ViewModel
import app.lade.draft.DraftMode
import app.lade.draft.DraftModel
import app.lade.draft.internal.DraftApiImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class InputBarHostViewModel @Inject constructor(
    private val api: DraftApiImpl,
) : ViewModel() {

    val draft: StateFlow<DraftModel> = api.draft
    val mode: StateFlow<DraftMode> = api.mode

    fun setDefaultDate(date: LocalDate?) {
        api.defaultDate = date
    }

    fun onTextChange(text: String) = api.updateTitle(text)

    fun onSubmit() = api.save()

    fun onExpand() = api.openEditor(null)

    fun onCancel() = api.cancel()
}