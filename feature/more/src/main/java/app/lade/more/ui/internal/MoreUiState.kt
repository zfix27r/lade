package app.lade.more.ui.internal

data class MoreUiState(
    val openCount: Int = 0,
    val dueHabitTotal: Int = 0,
    val dueHabitDone: Int = 0,
    val previewTitles: List<String> = emptyList(),
)