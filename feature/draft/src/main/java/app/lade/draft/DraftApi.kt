package app.lade.draft

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface DraftApi {

    /**
     * Запросить открытие расширенного редактора.
     * Модуль не навигирует — эмитит DraftEvent.OpenEditor.
     * entryId = null — редактировать текущий черновик.
     * entryId != null — открыть существующую запись.
     */
    fun openEditor(entryId: Long? = null)

    /**
     * Сбросить черновик полностью: пустой draft, mode = IDLE.
     */
    fun reset()

    /**
     * Режим панели.
     */
    val mode: StateFlow<DraftMode>

    /**
     * События: Saved, Error, OpenEditor, Cancelled.
     */
    val events: SharedFlow<DraftEvent>
}