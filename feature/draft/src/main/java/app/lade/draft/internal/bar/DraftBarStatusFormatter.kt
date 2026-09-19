package app.lade.draft.internal.bar

import app.lade.draft.DraftModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

internal object DraftBarStatusFormatter {

    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d MMMM", Locale("ru"))

    private val timeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())

    fun format(draft: DraftModel): String {
        val parts = mutableListOf<String>()

        formatDate(draft)?.let { parts += it }
        formatTime(draft)?.let { parts += it }
        formatRecurrence(draft)?.let { parts += it }
        formatGoals(draft)?.let { parts += it }

        return parts.joinToString(" · ")
    }

    private fun formatDate(draft: DraftModel): String? {
        val from = draft.dateFrom ?: return null
        val to = draft.dateTo
        return if (to != null && to.isAfter(from)) {
            "${from.format(dateFormatter)}–${to.format(dateFormatter)}"
        } else {
            from.format(dateFormatter)
        }
    }

    private fun formatTime(draft: DraftModel): String? {
        val from = draft.timeFrom ?: return null
        val to = draft.timeEnd
        return if (to != null) {
            "${from.format(timeFormatter)}–${to.format(timeFormatter)}"
        } else {
            from.format(timeFormatter)
        }
    }

    private fun formatRecurrence(draft: DraftModel): String? {
        val rrule = draft.rrule ?: return null
        return when {
            rrule.contains("FREQ=DAILY") -> "ежедневно"
            rrule.contains("FREQ=WEEKLY") -> "еженедельно"
            rrule.contains("FREQ=MONTHLY") -> "ежемесячно"
            rrule.contains("FREQ=YEARLY") -> "ежегодно"
            else -> null
        }
    }

    private fun formatGoals(draft: DraftModel): String? {
        val count = draft.goals.size
        if (count == 0) return null
        return when (count) {
            1 -> "1 цель"
            2, 3, 4 -> "$count цели"
            else -> "$count целей"
        }
    }
}