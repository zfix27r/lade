package app.lade.calendar.api.config

data class CalendarConfig(
    val list: ListConfig = DefaultListConfig,
    val strip: StripConfig = DefaultStripConfig,
    val week: WeekConfig = DefaultWeekConfig,
    val timelineWindow: TimelineWindowConfig = DefaultTimelineWindowConfig,
    val timelineLayout: TimelineLayoutConfig = DefaultTimelineLayoutConfig,
)

val DefaultCalendarConfig = CalendarConfig()