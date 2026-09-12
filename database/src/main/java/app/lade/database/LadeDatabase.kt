package app.lade.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.lade.database.category.CategoryDao
import app.lade.database.category.CategoryEntity
import app.lade.database.chat.ChatDictDao
import app.lade.database.chat.ChatDictEntity
import app.lade.database.chat.ChatMessageDao
import app.lade.database.chat.ChatMessageEntity
import app.lade.database.entry.EntryDao
import app.lade.database.entry.EntryEntity
import app.lade.database.entry.EntryHistoryDao
import app.lade.database.entry.EntryHistoryEntity

@Database(
	entities = [
		CategoryEntity::class,
		ChatDictEntity::class,
		ChatMessageEntity::class,
		EntryEntity::class,
		EntryHistoryEntity::class,
	],
	version = 14,
	exportSchema = false,
)
abstract class LadeDatabase : RoomDatabase() {
	abstract fun categoryDao(): CategoryDao
	abstract fun chatDictDao(): ChatDictDao
	abstract fun chatMessageDao(): ChatMessageDao
	abstract fun entryDao(): EntryDao
	abstract fun entryHistoryDao(): EntryHistoryDao
}