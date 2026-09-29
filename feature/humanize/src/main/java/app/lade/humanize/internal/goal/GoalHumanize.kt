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
        if (bodyShort.isEmpty() && bodyLong.isEmpty()) return Humanized("", "")

        val weightText = weight?.takeIf { it != 0.0 }?.let {
            val sign = if (it < 0) "-" else "+"
            val absValue = abs(it)
            val number = if (absValue % 1.0 == 0.0) absValue.toInt().toString() else absValue.toString()
            " $sign$number кг"
        }.orEmpty()

        val prefix = title.takeIf { it.isNotBlank() }?.let { "$it: " }.orEmpty()
        val short = "$prefix$bodyShort$weightText"
        val long = "$prefix$bodyLong$weightText"
        return Humanized(short = short, long = long)
    }

    private fun shortUnitLabel(unit: GoalUnit): String =
        context.getString(unit.shortLabelRes())

    private fun longUnitLabel(unit: GoalUnit): String =
        context.getString(unit.labelRes())
}