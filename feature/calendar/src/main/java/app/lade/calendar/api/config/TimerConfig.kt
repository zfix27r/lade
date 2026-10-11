package app.lade.calendar.api.config

data class TimerConfig(
    val resetMultiplier: Float = 2.0f,
)

val DefaultTimerConfig = TimerConfig()