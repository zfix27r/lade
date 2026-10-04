package app.lade.backup.internal.storage

import android.content.Context
import android.net.Uri
import app.lade.backup.api.BackupProgress
import app.lade.backup.api.BackupResult
import app.lade.database.DatabaseControl
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

@Singleton
internal class StorageExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val databaseControl: DatabaseControl,
    private val resolver: StorageFileResolver,
) {

    private val progressFlow = MutableSharedFlow<BackupProgress>(extraBufferCapacity = 16)
    val progress: SharedFlow<BackupProgress> = progressFlow.asSharedFlow()

    suspend fun export(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            progressFlow.tryEmit(BackupProgress.Preparing)
            databaseControl.checkpoint()

            progressFlow.tryEmit(BackupProgress.Copying)
            val files = resolver.databaseFiles()
            if (files.isEmpty()) {
                return@withContext BackupResult.Failure("Database file not found")
            }

            val out = context.contentResolver.openOutputStream(uri)
                ?: return@withContext BackupResult.Failure("Cannot open output stream")
            BufferedOutputStream(out).use { buffered ->
                ZipOutputStream(buffered).use { zip ->
                    for (file in files) {
                        zip.putNextEntry(ZipEntry(file.name))
                        file.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                }
            }
            progressFlow.tryEmit(BackupProgress.Finalizing)
            BackupResult.Success
        } catch (t: Throwable) {
            BackupResult.Failure(t.message ?: "Export failed")
        }
    }
}