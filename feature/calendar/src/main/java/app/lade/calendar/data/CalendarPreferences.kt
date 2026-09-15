package app.lade.calendar.data

import android.content.Context
import app.lade.calendar.domain.CalendarSavedState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit


@Singleton
class CalendarPreferences @Inject constructor(
	@ApplicationContext context: Context,
) {
	private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
	private val state = MutableStateFlow(read())

	val savedState: Flow<CalendarSavedState> = state.asStateFlow()

	suspend fun save(viewName: String, dateEpochDay: Long) {
		withContext(Dispatchers.IO) {
			prefs.edit {
                putString(KEY_VIEW, viewName)
                    .putLong(KEY_DATE, dateEpochDay)
            }
			state.value = CalendarSavedState(
				viewName = viewName,
				dateEpochDay = dateEpochDay,
				isStored = true,
			)
		}
	}

	private fun read(): CalendarSavedState {
		val stored = prefs.contains(KEY_VIEW)
		return CalendarSavedState(
			viewName = prefs.getString(KEY_VIEW, null) ?: "Day",
			dateEpochDay = if (prefs.contains(KEY_DATE)) {
				prefs.getLong(KEY_DATE, LocalDate.now().toEpochDay())
			} else {
				LocalDate.now().toEpochDay()
			},
			isStored = stored,
		)
	}

	companion object {
		private const val PREFS_NAME = "calendar_prefs"
		private const val KEY_VIEW = "view"
		private const val KEY_DATE = "date_epoch"
	}
}
