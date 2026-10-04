package app.lade.more.ui.internal.backup

sealed interface BackupUiProgress {
    data object Idle : BackupUiProgress
    data object Preparing : BackupUiProgress
    data object Copying : BackupUiProgress
    data object Finalizing : BackupUiProgress
}