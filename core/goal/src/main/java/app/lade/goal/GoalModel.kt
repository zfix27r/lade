package app.lade.goal

data class GoalModel(
    val amount: Int,
    val unit: Unit,
    val title: String? = null,
    val repeat: Int? = null,
    val weight: Double? = null,
)