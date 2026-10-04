package app.lade.backup.api

sealed interface BackupResult {
    data object Success : BackupResult
    data object RestartRequired : BackupResult
    data class Failure(val reason: String) : BackupResult
}