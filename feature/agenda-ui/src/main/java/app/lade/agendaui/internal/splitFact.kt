package app.lade.agendaui.internal

internal fun splitFact(factTotal: Int, amount: Int?, repeat: Int?): Pair<Int, Int> {
    val a = amount ?: return factTotal to 0
    val r = repeat ?: 1
    if (a <= 0) return 0 to factTotal
    val fullSets = factTotal / a
    val remainder = factTotal % a
    return when {
        fullSets >= r -> a to r
        remainder == 0 -> a to fullSets
        else -> remainder to fullSets
    }
}