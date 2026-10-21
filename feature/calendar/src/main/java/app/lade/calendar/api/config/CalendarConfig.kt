package app.lade.calendar.api.config

data class CalendarConfig(
    val list: ListConfig = DefaultListConfig,
    val strip: StripConfig = DefaultStripConfig,
    val week: WeekConfig = DefaultWeekConfig,
    val month: MonthConfig = DefaultMonthConfig,
    val timelineWindow: TimelineWindowConfig = DefaultTimelineWindowConfig,
    val timelineLayout: TimelineLayoutConfig = DefaultTimelineLayoutConfig,
    val timer: TimerConfig = DefaultTimerConfig,
    val card: CalendarCardConfig = DefaultCalendarCardConfig,
    val dates: CalendarDatesConfig = DefaultCalendarDatesConfig,
)

val DefaultCalendarConfig = CalendarConfig()