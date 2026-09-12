package app.lade.entry.domain

import app.lade.entry.domain.models.EntryKind

object KindPriority {
	val DEFAULT: List<EntryKind> = listOf(
		EntryKind.SCHEDULE,
		EntryKind.HABIT,
		EntryKind.TASK,
		EntryKind.EVENT,
	)

	fun rank(kind: EntryKind, order: List<EntryKind> = DEFAULT): Int {
		val i = order.indexOf(kind)
		return if (i >= 0) i else order.size
	}
}
