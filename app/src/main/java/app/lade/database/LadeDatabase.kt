package app.lade.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.lade.database.dao.CategoryDao
import app.lade.database.dao.HabitDao
import app.lade.database.dao.HabitHistoryDao
import app.lade.database.dao.TimeBlockDao
import app.lade.database.dao.TimeScheduleDao
import app.lade.database.entity.CategoryEntity
import app.lade.database.entity.HabitEntity
import app.lade.database.entity.HabitHistoryEntity
import app.lade.database.entity.TimeBlockEntity
import app.lade.database.entity.TimeScheduleEntity

@Database(
	entities = [
		CategoryEntity::class,
		HabitEntity::class,
		HabitHistoryEntity::class,
		TimeScheduleEntity::class,
		TimeBlockEntity::class,
	],
	version = 4,
	exportSchema = false,
)
abstract class LadeDatabase : RoomDatabase() {
	abstract fun categoryDao(): CategoryDao
	abstract fun habitDao(): HabitDao
	abstract fun habitHistoryDao(): HabitHistoryDao
	abstract fun timeScheduleDao(): TimeScheduleDao
	abstract fun timeBlockDao(): TimeBlockDao
}
