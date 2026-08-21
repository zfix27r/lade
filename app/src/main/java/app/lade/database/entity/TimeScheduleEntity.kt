package app.lade.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "time_schedules",
	foreignKeys = [
		ForeignKey(
			entity = CategoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.RESTRICT,
		),
	],
	indices = [Index("categoryId")],
)
data class TimeScheduleEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val title: String,
	val categoryId: Long,
	/** Epoch day (LocalDate.toEpochDay()). Query columns for period overlap. */
	val dateFromEpochDay: Long,
	val dateToEpochDay: Long,
	/** RFC 5545 RRULE body, e.g. FREQ=WEEKLY;BYDAY=MO,TU,WE,TH,FR */
	val rrule: String,
	/** Minutes from midnight. Occupancy, not recurrence. */
	val startTimeMinutes: Int,
	val endTimeMinutes: Int,
	val archivedAtEpochMs: Long? = null,
)
