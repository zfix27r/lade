package app.lade.feed.domain

import kotlinx.coroutines.flow.Flow

interface FeedRepository {
	fun observeState(): Flow<FeedState>
}
