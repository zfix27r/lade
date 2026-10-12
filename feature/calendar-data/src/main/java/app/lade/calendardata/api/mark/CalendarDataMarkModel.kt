package app.lade.calendardata.api.mark

data class CalendarDataMarkModel(
    val total: Int,
    val done: Int,
) {
    val fraction: Float
        get() = if (total == 0) 0f else done.toFloat() / total
}