package app.lade.di

import android.content.Context
import app.lade.agendastore.entry.EntryDao
import app.lade.agendastore.goal.GoalDao
import app.lade.agendastore.log.LogDao
import app.lade.categorystore.CategoryDao
import app.lade.chatstore.ChatDictDao
import app.lade.chatstore.ChatMessageDao
import app.lade.database.DatabaseControl
import app.lade.database.Transaction
import app.lade.db.DatabaseControlImpl
import app.lade.db.LadeDatabase
import app.lade.db.LadeDatabaseHolder
import app.lade.db.TransactionImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DatabaseModule {

	@Binds
	@Singleton
	internal abstract fun bindDatabaseControl(impl: DatabaseControlImpl): DatabaseControl

	companion object {

		@Provides
		@Singleton
		internal fun provideLadeDatabase(
			@ApplicationContext context: Context,
			holder: LadeDatabaseHolder,
		): LadeDatabase = holder.get(context)

		@Provides
		@Singleton
		internal fun provideTransaction(impl: TransactionImpl): Transaction = impl

		@Provides
		internal fun provideCategoryDao(db: LadeDatabase): CategoryDao = db.categoryDao()

		@Provides
		internal fun provideChatDictDao(db: LadeDatabase): ChatDictDao = db.chatDictDao()

		@Provides
		internal fun provideChatMessageDao(db: LadeDatabase): ChatMessageDao = db.chatMessageDao()

		@Provides
		internal fun provideEntryDao(db: LadeDatabase): EntryDao = db.entryDao()

		@Provides
		@Singleton
		internal fun provideGoalDao(db: LadeDatabase): GoalDao = db.goalDao()

		@Provides
		@Singleton
		internal fun provideLogDao(db: LadeDatabase): LogDao = db.logDao()
	}
}