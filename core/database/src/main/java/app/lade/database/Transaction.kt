package app.lade.database

interface Transaction {
    suspend fun <T> runIn(block: suspend () -> T): T
}