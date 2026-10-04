package app.lade.db

import android.content.Context
import app.lade.database.DatabaseControl
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class DatabaseControlImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val holder: LadeDatabaseHolder,
) : DatabaseControl {

    private val mutex = Mutex()

    override suspend fun <T> withClosed(block: suspend () -> T): T =
        mutex.withLock {
            holder.close()
            try {
                block()
            } finally {
                holder.reopen(context)
            }
        }

    override suspend fun close() = mutex.withLock { holder.close() }

    override suspend fun reopen() = mutex.withLock { holder.reopen(context) }

    override suspend fun closeForRestart() = mutex.withLock { holder.close() }

    override suspend fun checkpoint() {
        mutex.withLock {
            val db = holder.get(context)
            db.query("PRAGMA wal_checkpoint(FULL)", null).use { cursor ->
                cursor.moveToFirst()
            }
        }
    }

    override fun databaseName(): String = LadeDatabase.NAME
}