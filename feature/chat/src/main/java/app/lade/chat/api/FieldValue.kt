package app.lade.chat.api

import app.lade.draftdata.DraftGoal
import app.lade.entrykind.EntryKind
import java.time.LocalDate
import java.time.LocalTime

sealed interface FieldValue {
    data class Kind(
        val value: EntryKind,
        val systemKey: String? = null,
    ) : FieldValue
    data class Date(val value: LocalDate) : FieldValue
    data class Time(val value: LocalTime) : FieldValue
    data class Goal(val value: DraftGoal) : FieldValue
    data class Text(val value: String) : FieldValue
    data class Number(val value: Int) : FieldValue
}