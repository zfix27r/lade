package app.lade.backup.internal.storage

import android.content.Context
import app.lade.database.DatabaseControl
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StorageFileResolver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val databaseControl: DatabaseControl,
) {

    fun databaseFiles(): List<File> {
        val name = databaseControl.databaseName()
        val dir = context.getDatabasePath(name).parentFile ?: return emptyList()
        return listOf(
            File(dir, name),
            File(dir, "$name-wal"),
            File(dir, "$name-shm"),
        ).filter { it.exists() }
    }

    fun databaseFile(): File =
        context.getDatabasePath(databaseControl.databaseName())
}