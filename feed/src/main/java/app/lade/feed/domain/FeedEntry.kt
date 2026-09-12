package app.lade.feed.domain

data class FeedEntry(
	val id: Long,
	val kind: String,
	val title: String,
	val result: String?,
)
