package app.lade.database

interface DatabaseControl {
    suspend fun <T> withClosed(block: suspend () -> T): T
    suspend fun close()
    suspend fun reopen()
    suspend fun closeForRestart()
    suspend fun checkpoint()
    fun databaseName(): String
}