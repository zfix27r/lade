package app.lade.humanize.internal.goal

import android.content.Context
import app.lade.agenda.api.goal.GoalUnit
import app.lade.agenda.api.goal.labelRes
import app.lade.humanize.api.Humanized
import kotlin.math.abs

internal class GoalHumanize(
    private val context: Context,
) {
    fun format(
        title: String,
        unit: GoalUnit,
        amount: Int?,
        repeat: Int?,
        weight: Double?,
    ): Humanized {
        val body = when {
            unit == GoalUnit.REP && amount != null && repeat != null -> "${amount}х$repeat"
            amount != null && amount > 0 && unit != GoalUnit.UNKNOWN ->
                "$amount ${unitLabel(unit)}"
            else -> ""
        }
        if (body.isEmpty()) return Humanized("", "")

        val weightText = weight?.takeIf { it != 0.0 }?.let {
            val sign = if (it < 0) "-" else "+"
            val absValue = abs(it)
            val number = if (absValue % 1.0 == 0.0) absValue.toInt().toString() else absValue.toString()
            " $sign$number кг"
        }.orEmpty()

        val prefix = title.takeIf { it.isNotBlank() }?.let { "$it: " }.orEmpty()
        val text = "$prefix$body$weightText"
        return Humanized(short = text, long = text)
    }

    private fun unitLabel(unit: GoalUnit): String =
        context.getString(unit.labelRes())
}