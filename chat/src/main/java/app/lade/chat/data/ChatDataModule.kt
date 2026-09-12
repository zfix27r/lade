package app.lade.chat.data

import app.lade.chat.domain.ChatCorpusRepository
import app.lade.chat.domain.ChatDictRepository
import app.lade.chat.domain.ChatHistoryRepository
import app.lade.chat.domain.ChatMatchCatalog
import app.lade.chat.domain.ChatParseEnhancer
import app.lade.chat.domain.IdentityChatParseEnhancer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatDataModule {
	@Binds
	@Singleton
	abstract fun bindChatDictRepository(impl: ChatDictRepositoryImpl): ChatDictRepository

	@Binds
	@Singleton
	abstract fun bindChatCorpusRepository(impl: ChatCorpusRepositoryImpl): ChatCorpusRepository

	@Binds
	@Singleton
	abstract fun bindChatMatchCatalog(impl: ChatMatchCatalogImpl): ChatMatchCatalog

	@Binds
	@Singleton
	abstract fun bindChatHistoryRepository(impl: ChatHistoryRepositoryImpl): ChatHistoryRepository

	@Binds
	@Singleton
	abstract fun bindChatParseEnhancer(impl: IdentityChatParseEnhancer): ChatParseEnhancer
}
