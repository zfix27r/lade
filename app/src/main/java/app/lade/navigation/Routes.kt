package app.lade.navigation

import android.net.Uri

object Routes {
	const val More = "more"
	const val Devices = "devices"
	const val Calsync = "calsync"

	const val Categories = "categories"
	const val CategoryEdit = "categories/edit/{categoryId}"

	const val Entries = "entries"
	const val EntryEdit = "entries/edit/{entryId}/{kind}?dateEpochDay={dateEpochDay}&titleHint={titleHint}"

	const val Calendar = "calendar"
	const val Chat = "chat"
	const val ChatDicts = "chat/dicts"
	const val ChatDictEdit = "chat/dicts/edit/{dictId}"
	const val DayPartSettings = "reminders/daypart"
	const val KindPrioritySettings = "settings/kind-priority"

	val bottomBarRoutes = setOf(Calendar, Chat, More)

	fun categoryEdit(id: Long = -1L) = "categories/edit/$id"
	fun entryEdit(
		id: Long = -1L,
		kind: String = "task",
		dateEpochDay: Long = -1L,
		titleHint: String = "",
	) = "entries/edit/$id/$kind?dateEpochDay=$dateEpochDay&titleHint=${Uri.encode(titleHint)}"
	fun chatDictEdit(id: Long = -1L) = "chat/dicts/edit/$id"
}