package app.lade.backup.api

sealed interface BackupProgress {
    data object Preparing : BackupProgress
    data object Copying : BackupProgress
    data object Finalizing : BackupProgress
}