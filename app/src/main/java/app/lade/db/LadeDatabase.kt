package app.lade.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.lade.categorystore.CategoryDao
import app.lade.categorystore.CategoryEntity
import app.lade.chatstore.ChatDictDao
import app.lade.chatstore.ChatDictEntity
import app.lade.chatstore.ChatMessageDao
import app.lade.chatstore.ChatMessageEntity
import app.lade.entrystore.entry.EntryDao
import app.lade.entrystore.entry.EntryEntity
import app.lade.entrystore.log.LogDao
import app.lade.entrystore.log.LogEntity


@Database(
	entities = [
		CategoryEntity::class,
		ChatDictEntity::class,
		ChatMessageEntity::class,
		EntryEntity::class,
		LogEntity::class,
	],
	version = 14,
	exportSchema = false,
)
abstract class LadeDatabase : RoomDatabase() {
	abstract fun categoryDao(): CategoryDao
	abstract fun chatDictDao(): ChatDictDao
	abstract fun chatMessageDao(): ChatMessageDao
	abstract fun entryDao(): EntryDao
	abstract fun entryHistoryDao(): LogDao
}