package app.lade.chat.di

import app.lade.chat.api.ChatApi
import app.lade.chat.internal.ChatApiImpl
import app.lade.chat.internal.pipeline.goal.GoalListExtractor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ChatDataModule {

    @Binds
    @Singleton
    internal abstract fun bindChatApi(impl: ChatApiImpl): ChatApi
}