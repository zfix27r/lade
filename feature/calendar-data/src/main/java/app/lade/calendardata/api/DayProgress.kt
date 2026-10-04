package app.lade.calendardata.api

data class DayProgress(
    val total: Int,
    val done: Int,
) {
    val fraction: Float
        get() = if (total == 0) 0f else done.toFloat() / total
}