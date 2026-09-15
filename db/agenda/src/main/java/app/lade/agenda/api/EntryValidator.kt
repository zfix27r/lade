package app.lade.agenda.api

import app.lade.agenda.api.entry.EntryMissingField
import app.lade.agenda.api.entry.EntryModel

interface EntryValidator {
    suspend fun validate(entry: EntryModel): EntryMissingField?
}