package app.lade.draft.internal

import app.lade.draft.DraftApi
import app.lade.draft.internal.store.DraftStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DraftApiImpl @Inject constructor(
    private val store: DraftStore,
) : DraftApi {

    override fun open(entryId: Long?) {
        store.open(entryId)
    }

    override fun reset() {
        store.reset()
    }
}