package app.lade.backup.internal

import android.net.Uri
import app.lade.backup.api.BackupFormat
import app.lade.backup.api.BackupProgress
import app.lade.backup.api.BackupResult
import app.lade.backup.internal.storage.StorageExporter
import app.lade.backup.internal.storage.StorageImporter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

@Singleton
internal class BackupCoordinatorImpl @Inject constructor(
    private val exporter: StorageExporter,
    private val importer: StorageImporter,
) : BackupCoordinator {

    override val progress: Flow<BackupProgress> = merge(exporter.progress, importer.progress)

    override suspend fun export(uri: String, format: BackupFormat): BackupResult {
        val parsed = Uri.parse(uri)
        return when (format) {
            BackupFormat.DB -> exporter.export(parsed)
        }
    }

    override suspend fun import(uri: String, format: BackupFormat): BackupResult {
        val parsed = Uri.parse(uri)
        return when (format) {
            BackupFormat.DB -> importer.import(parsed)
        }
    }
}