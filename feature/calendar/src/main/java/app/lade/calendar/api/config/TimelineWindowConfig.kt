package app.lade.calendar.api.config

data class TimelineWindowConfig(
    val pastDays: Long = 30L,
    val futureDays: Long = 60L,
    val pageDays: Long = 30L,
    val loadThresholdDays: Long = 10L,
)

val DefaultTimelineWindowConfig = TimelineWindowConfig()