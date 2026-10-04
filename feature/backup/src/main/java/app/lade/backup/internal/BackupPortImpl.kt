package app.lade.backup.internal

import app.lade.backup.api.BackupFormat
import app.lade.backup.api.BackupPort
import app.lade.backup.api.BackupProgress
import app.lade.backup.api.BackupResult
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
internal class BackupPortImpl @Inject constructor(
    private val coordinator: BackupCoordinator,
) : BackupPort {

    override val progress: Flow<BackupProgress> = coordinator.progress

    override suspend fun export(uri: String, format: BackupFormat): BackupResult =
        coordinator.export(uri, format)

    override suspend fun import(uri: String, format: BackupFormat): BackupResult =
        coordinator.import(uri, format)
}