package app.lade.categories.domain.model

data class Category(
	val id: Long = 0,
	val key: String? = null,
	val title: String,
	val color: String,
	val archivedAtEpochMs: Long? = null,
) {
	val isArchived: Boolean get() = archivedAtEpochMs != null
}

object CategoryColors {
	val ALL = listOf(
		"slate", "stone", "red", "orange", "amber", "green",
		"teal", "sky", "blue", "violet", "pink",
	)
}
