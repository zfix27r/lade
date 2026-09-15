package app.lade.schedule.data

import app.lade.daypart.domain.DayPartClock
import app.lade.temporal.api.RecurrencePreset

data class TemporalOptionsConfig(
    val showDate: Boolean = false,
    val showTime: Boolean = false,
    val showTimeRange: Boolean = false,
    val showDateRange: Boolean = false,
    val showRecurrence: Boolean = false,
    val recurrencePresets: List<RecurrencePreset> = RecurrencePreset.HABIT,
    val showAlarm: Boolean = false,
    val showReminder: Boolean = false,
    /** Wrap each temporal block in an outlined card (create/edit sections). */
    val useSectionCards: Boolean = false,
    /** When set with [showTime], shows morning/midday/evening chips (prefs from reminders). */
    val dayPartClock: DayPartClock? = null,
)
