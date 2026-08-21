package app.lade.database

import android.content.Context
import androidx.room.Room
import app.lade.database.dao.CategoryDao
import app.lade.database.dao.HabitDao
import app.lade.database.dao.HabitHistoryDao
import app.lade.database.dao.TimeBlockDao
import app.lade.database.dao.TimeScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	@Provides
	@Singleton
	fun provideDatabase(
		@ApplicationContext context: Context,
	): LadeDatabase =
		Room.databaseBuilder(context, LadeDatabase::class.java, "lade.db")
			.fallbackToDestructiveMigration(dropAllTables = true)
			.build()

	@Provides
	fun provideCategoryDao(db: LadeDatabase): CategoryDao = db.categoryDao()

	@Provides
	fun provideHabitDao(db: LadeDatabase): HabitDao = db.habitDao()

	@Provides
	fun provideHabitHistoryDao(db: LadeDatabase): HabitHistoryDao = db.habitHistoryDao()

	@Provides
	fun provideTimeScheduleDao(db: LadeDatabase): TimeScheduleDao = db.timeScheduleDao()

	@Provides
	fun provideTimeBlockDao(db: LadeDatabase): TimeBlockDao = db.timeBlockDao()
}
