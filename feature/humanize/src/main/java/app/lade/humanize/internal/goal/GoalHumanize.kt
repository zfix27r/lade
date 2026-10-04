package app.lade.humanize.internal.goal

import android.content.Context
import app.lade.goal.GoalUnit
import app.lade.goal.labelRes
import app.lade.goal.shortLabelRes
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
        val details = formatDetails(unit, amount, repeat, weight)
        if (details.short.isEmpty() && details.long.isEmpty()) return Humanized("", "")
        val prefix = title.takeIf { it.isNotBlank() }?.let { "$it: " }.orEmpty()
        return Humanized(
            short = "$prefix${details.short}",
            long = "$prefix${details.long}",
        )
    }

    fun formatDetails(
        unit: GoalUnit,
        amount: Int?,
        repeat: Int?,
        weight: Double?,
    ): Humanized {
        val bodyShort = when {
            unit == GoalUnit.REP && amount != null && repeat != null -> "${amount}х$repeat"
            amount != null && amount > 0 && unit != GoalUnit.UNKNOWN ->
                "$amount ${shortUnitLabel(unit)}"
            else -> ""
        }
        val bodyLong = when {
            unit == GoalUnit.REP && amount != null && repeat != null -> "${amount}х$repeat"
            amount != null && amount > 0 && unit != GoalUnit.UNKNOWN ->
                "$amount ${longUnitLabel(unit)}"
            else -> ""
        }
        if (bodyShort.isEmpty() && bodyLong.isEmpty() && weight == null) {
            return Humanized("", "")
        }

        val weightText = weight?.takeIf { it != 0.0 }?.let {
            val sign = if (it < 0) "-" else "+"
            val absValue = abs(it)
            val number = if (absValue % 1.0 == 0.0) absValue.toInt().toString() else absValue.toString()
            " $sign$number кг"
        }.orEmpty()

        val short = "$bodyShort$weightText".trim()
        val long = "$bodyLong$weightText".trim()
        return Humanized(short = short, long = long)
    }

    private fun shortUnitLabel(unit: GoalUnit): String =
        context.getString(unit.shortLabelRes())

    private fun longUnitLabel(unit: GoalUnit): String =
        context.getString(unit.labelRes())
}