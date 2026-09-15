package app.lade.di

import android.content.Context
import androidx.room.Room
import app.lade.categorystore.CategoryDao
import app.lade.chatstore.ChatDictDao
import app.lade.chatstore.ChatMessageDao
import app.lade.db.LadeDatabase
import app.lade.entrystore.entry.EntryDao
import app.lade.entrystore.log.LogDao
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
		Room.databaseBuilder<LadeDatabase>(
			context = context,
			name = "lade.db",
		)
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
	fun provideEntryHistoryDao(db: LadeDatabase): LogDao = db.entryHistoryDao()
}