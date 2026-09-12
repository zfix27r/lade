package app.lade.database.di

import android.content.Context
import androidx.room.Room
import app.lade.database.LadeDatabase
import app.lade.database.category.CategoryDao
import app.lade.database.chat.ChatDictDao
import app.lade.database.chat.ChatMessageDao
import app.lade.database.entry.EntryDao
import app.lade.database.entry.EntryHistoryDao
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
	fun provideChatDictDao(db: LadeDatabase): ChatDictDao = db.chatDictDao()

	@Provides
	fun provideChatMessageDao(db: LadeDatabase): ChatMessageDao = db.chatMessageDao()

	@Provides
	fun provideEntryDao(db: LadeDatabase): EntryDao = db.entryDao()

	@Provides
	fun provideEntryHistoryDao(db: LadeDatabase): EntryHistoryDao = db.entryHistoryDao()
}