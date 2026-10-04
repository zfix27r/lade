package app.lade.more.ui.internal.backup

data class BackupUiState(
    val isRunning: Boolean = false,
    val progress: BackupUiProgress = BackupUiProgress.Idle,
    val message: Int? = null,
    val showRestartDialog: Boolean = false,
)