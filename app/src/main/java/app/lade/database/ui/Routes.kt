package app.lade.ui

object Routes {
	const val More = "more"
	const val Devices = "devices"
	const val Calsync = "calsync"

	const val Categories = "categories"
	const val CategoryEdit = "categories/edit/{categoryId}"

	const val Habits = "habits"
	const val HabitEdit = "habits/edit/{habitId}"

	const val TimeSchedules = "time"
	const val TimeScheduleEdit = "time/edit/{scheduleId}"
	const val TimeBlockEdit = "time/block/edit/{blockId}/{dateEpochDay}"

	const val Calendar = "calendar"
	const val Chat = "chat"

	val bottomBarRoutes = setOf(Calendar, Chat, More)

	fun categoryEdit(id: Long = -1L) = "categories/edit/$id"
	fun habitEdit(id: Long = -1L) = "habits/edit/$id"
	fun timeScheduleEdit(id: Long = -1L) = "time/edit/$id"
	fun timeBlockEdit(blockId: Long = -1L, dateEpochDay: Long) =
		"time/block/edit/$blockId/$dateEpochDay"
}
