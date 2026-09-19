package app.lade.di

import android.content.Context
import androidx.room.Room
import app.lade.categorystore.CategoryDao
import app.lade.chatstore.ChatDictDao
import app.lade.chatstore.ChatMessageDao
import app.lade.db.LadeDatabase
import app.lade.agendastore.entry.EntryDao
import app.lade.agendastore.goal.GoalDao
import app.lade.agendastore.log.LogDao
import app.lade.database.Transaction
import app.lade.db.TransactionImpl
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
	@Singleton
	fun provideTransaction(impl: TransactionImpl): Transaction = impl

	@Provides
	fun provideCategoryDao(db: LadeDatabase): CategoryDao = db.categoryDao()

	@Provides
	fun provideChatDictDao(db: LadeDatabase): ChatDictDao = db.chatDictDao()

	@Provides
	fun provideChatMessageDao(db: LadeDatabase): ChatMessageDao = db.chatMessageDao()

	@Provides
	fun provideEntryDao(db: LadeDatabase): EntryDao = db.entryDao()

	@Provides
	@Singleton
	fun provideGoalDao(db: LadeDatabase): GoalDao = db.goalDao()

	@Provides
	@Singleton
	fun provideLogDao(db: LadeDatabase): LogDao = db.logDao()
}