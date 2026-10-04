package app.lade.db

import android.content.Context
import androidx.room.Room
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LadeDatabaseHolder @Inject constructor() {

    @Volatile
    private var instance: LadeDatabase? = null

    fun get(context: Context): LadeDatabase {
        val current = instance
        if (current != null && current.isOpen) return current
        return synchronized(this) {
            val again = instance
            if (again != null && again.isOpen) return again
            val created = Room.databaseBuilder(
                context.applicationContext,
                LadeDatabase::class.java,
                LadeDatabase.NAME,
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
            instance = created
            created
        }
    }

    fun close() {
        synchronized(this) {
            instance?.close()
            instance = null
        }
    }

    fun reopen(context: Context) {
        get(context)
    }
}