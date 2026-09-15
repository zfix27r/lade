package app.lade.categories.domain.model

data class Category(
	val id: Long = 0,
	val key: String? = null,
	val title: String,
	/** ColorKey preset or `#RRGGBB` / `#AARRGGBB`. */
	val color: String,
	val archivedAtEpochMs: Long? = null,
) {
	val isArchived: Boolean get() = archivedAtEpochMs != null
}

object CategoryColors {
	/** ~20 common presets for circle pickers. */
	val ALL = listOf(
		"slate", "stone", "brown", "red", "rose", "orange", "amber", "yellow",
		"lime", "green", "emerald", "teal", "cyan", "sky", "blue", "indigo",
		"violet", "purple", "fuchsia", "pink",
	)

	fun isPreset(value: String): Boolean = value in ALL

	private val HEX = Regex("^#([0-9A-Fa-f]{6}|[0-9A-Fa-f]{8})$")

	fun isCustomHex(value: String): Boolean = HEX.matches(value)
}
