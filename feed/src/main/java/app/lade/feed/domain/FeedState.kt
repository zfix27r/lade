package app.lade.feed.domain

data class FeedState(
	val dateEpochDay: Long,
	val entries: List<FeedEntry>,
)
