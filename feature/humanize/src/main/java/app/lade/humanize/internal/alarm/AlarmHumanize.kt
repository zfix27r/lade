package app.lade.humanize.internal.alarm

import android.content.Context
import app.lade.humanize.R
import app.lade.humanize.api.Humanized
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class AlarmHumanize(
    private val context: Context,
) {
    fun format(time: LocalTime, mode: String): Humanized {
        val timeText = time.format(FORMATTER)
        val modeShort = modeShort(mode)
        val modeLong = modeLong(mode)

        val short = if (modeShort.isNullOrBlank()) {
            context.getString(R.string.humanize_alarm_short, timeText)
        } else {
            context.getString(R.string.humanize_alarm_short_with_mode, timeText, modeShort)
        }

        val long = if (modeLong.isNullOrBlank()) {
            context.getString(R.string.humanize_alarm_long, timeText)
        } else {
            context.getString(R.string.humanize_alarm_long_with_mode, timeText, modeLong)
        }

        return Humanized(short = short, long = long)
    }

    private fun modeShort(mode: String): String? = when (mode.lowercase()) {
        "sound" -> context.getString(R.string.humanize_alarm_mode_sound_short)
        "vibrate" -> context.getString(R.string.humanize_alarm_mode_vibrate_short)
        "silent" -> context.getString(R.string.humanize_alarm_mode_silent_short)
        else -> null
    }

    private fun modeLong(mode: String): String? = when (mode.lowercase()) {
        "sound" -> context.getString(R.string.humanize_alarm_mode_sound_long)
        "vibrate" -> context.getString(R.string.humanize_alarm_mode_vibrate_long)
        "silent" -> context.getString(R.string.humanize_alarm_mode_silent_long)
        else -> null
    }

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}