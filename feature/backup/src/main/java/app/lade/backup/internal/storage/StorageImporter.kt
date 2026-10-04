package app.lade.backup.internal.storage

import android.content.Context
import android.net.Uri
import app.lade.backup.api.BackupProgress
import app.lade.backup.api.BackupResult
import app.lade.database.DatabaseControl
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedInputStream
import java.io.File
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

@Singleton
internal class StorageImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val databaseControl: DatabaseControl,
    private val resolver: StorageFileResolver,
) {

    private val progressFlow = MutableSharedFlow<BackupProgress>(extraBufferCapacity = 16)
    val progress: SharedFlow<BackupProgress> = progressFlow.asSharedFlow()

    suspend fun import(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        try {
            progressFlow.tryEmit(BackupProgress.Preparing)
            databaseControl.closeForRestart()

            progressFlow.tryEmit(BackupProgress.Copying)
            val targetDir = resolver.databaseFile().parentFile
                ?: return@withContext BackupResult.Failure("Database dir not found")
            if (!targetDir.exists()) targetDir.mkdirs()

            deleteExisting(targetDir)

            val input = context.contentResolver.openInputStream(uri)
                ?: return@withContext BackupResult.Failure("Cannot open input stream")
            BufferedInputStream(input).use { buffered ->
                ZipInputStream(buffered).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        val outFile = File(targetDir, entry.name)
                        outFile.outputStream().use { zip.copyTo(it) }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            }
            progressFlow.tryEmit(BackupProgress.Finalizing)
            BackupResult.RestartRequired
        } catch (t: Throwable) {
            BackupResult.Failure(t.message ?: "Import failed")
        }
    }

    private fun deleteExisting(dir: File) {
        val name = databaseControl.databaseName()
        listOf(
            File(dir, name),
            File(dir, "$name-wal"),
            File(dir, "$name-shm"),
        ).forEach { if (it.exists()) it.delete() }
    }
}