package app.lade.db

import androidx.room.withTransaction
import app.lade.database.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionImpl @Inject constructor(
    private val db: LadeDatabase,
) : Transaction {
    override suspend fun <T> runIn(block: suspend () -> T): T =
        db.withTransaction { block() }
}