package app.lade.agendastore.entry

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "entries",
	indices = [
		Index("templateId"),
		Index("kind"),
	],
)
data class EntryEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val kind: String,
	val title: String,
	val templateId: Long?,
	val dateFromEpochDay: Long? = null,
	val dateToEpochDay: Long? = null,
	val startTimeMinutes: Int? = null,
	val endTimeMinutes: Int? = null,
	val rrule: String? = null,
	val alarmMode: String = "none",
	val reminderMinutesBefore: Int? = null,
	val archivedAtEpochMs: Long? = null,
	val pausedAtEpochMs: Long? = null,
	val createdAtEpochMs: Long,
	val updatedAtEpochMs: Long? = null,
)