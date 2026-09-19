package app.lade.navigation

import android.net.Uri

object Routes {
	const val Settings = "more"
	const val Devices = "devices"
	const val Calsync = "calsync"

	const val Categories = "categories"
	const val CategoryEdit = "categories/edit/{categoryId}"

	const val Entries = "entries"

	const val Calendar = "calendar"
	const val Chat = "chat"
	const val ChatDicts = "chat/dicts"
	const val ChatDictEdit = "chat/dicts/edit/{dictId}"
	const val DayPartSettings = "reminders/daypart"
	const val KindPrioritySettings = "settings/kind-priority"

	const val DraftEditor = "draft/editor"

	val bottomBarRoutes = setOf(Calendar, Chat, Settings)

	fun categoryEdit(id: Long = -1L) = "categories/edit/$id"

	fun chatDictEdit(id: Long = -1L) = "chat/dicts/edit/$id"
}