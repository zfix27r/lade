package app.lade.navigation

object Routes {
	const val Settings = "more"
	const val Devices = "devices"
	const val Calsync = "calsync"

	const val Categories = "categories"
	const val CategoryEdit = "categories/edit/{categoryId}"

	const val Calendar = "calendar"
	const val DayPartSettings = "reminders/daypart"
	const val KindPrioritySettings = "settings/kind-priority"

	const val DraftEditor = "draft/editor"


	fun categoryEdit(id: Long = -1L) = "categories/edit/$id"
}