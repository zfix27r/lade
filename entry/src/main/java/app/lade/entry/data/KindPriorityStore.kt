package app.lade.entry.data

import android.content.Context
import app.lade.entry.domain.KindPriority
import app.lade.entry.domain.KindPriorityRepository
import app.lade.entry.domain.models.EntryKind
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persists display / layer order of [app.lade.entrydetailsscreen.domain.EntryKind]. Default: schedule < habit < task < event.
 */
@Singleton
class KindPriorityStore @Inject constructor(
	@ApplicationContext context: Context,
) : KindPriorityRepository {
	private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
	private val state = MutableStateFlow(read())

	override val order: Flow<List<EntryKind>> = state.asStateFlow()

	override fun current(): List<EntryKind> = state.value

	override suspend fun setOrder(kinds: List<EntryKind>) {
		require(kinds.toSet() == EntryKind.entries.toSet()) {
			"order must include each EntryKind once"
		}
		withContext(Dispatchers.IO) {
			prefs.edit().putString(KEY_ORDER, kinds.joinToString(",") { it.storage }).apply()
			state.value = kinds
		}
	}

	private fun read(): List<EntryKind> {
		val raw = prefs.getString(KEY_ORDER, null) ?: return KindPriority.DEFAULT
		val parsed = raw.split(',')
			.map { it.trim() }
			.filter { it.isNotEmpty() }
			.mapNotNull { token -> EntryKind.entries.find { it.storage == token } }
		return if (parsed.toSet() == EntryKind.entries.toSet() && parsed.size == EntryKind.entries.size) {
			parsed
		} else {
			KindPriority.DEFAULT
		}
	}

	companion object {
		private const val PREFS = "entry_kind_priority"
		private const val KEY_ORDER = "order"
	}
}
