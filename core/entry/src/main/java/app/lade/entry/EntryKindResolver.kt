package app.lade.entry

interface EntryKindResolver {
    fun resolve(input: EntryModel): EntryKind
}