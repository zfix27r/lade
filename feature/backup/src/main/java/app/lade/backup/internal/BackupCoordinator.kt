package app.lade.backup.internal

import app.lade.backup.api.BackupFormat
import app.lade.backup.api.BackupProgress
import app.lade.backup.api.BackupResult
import kotlinx.coroutines.flow.Flow

interface BackupCoordinator {
    val progress: Flow<BackupProgress>
    suspend fun export(uri: String, format: BackupFormat): BackupResult
    suspend fun import(uri: String, format: BackupFormat): BackupResult
}