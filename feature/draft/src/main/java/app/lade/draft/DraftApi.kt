package app.lade.draft

interface DraftApi {
    fun open(entryId: Long?)
    fun reset()
}