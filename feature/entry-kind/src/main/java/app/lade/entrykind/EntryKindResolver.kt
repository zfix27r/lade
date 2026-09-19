package app.lade.entrykind

interface EntryKindResolver {
    fun resolve(input: EntryKindInput): EntryKind
}