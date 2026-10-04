package app.lade.backup.api

import kotlinx.coroutines.flow.Flow

interface BackupPort {
    val progress: Flow<BackupProgress>
    suspend fun export(uri: String, format: BackupFormat): BackupResult
    suspend fun import(uri: String, format: BackupFormat): BackupResult
}