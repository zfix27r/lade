package app.lade.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "habits",
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
data class HabitEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val title: String,
	val categoryId: Long,
	/** RFC 5545 RRULE body, e.g. FREQ=WEEKLY;BYDAY=MO,WE,FR */
	val rrule: String,
	val goalValue: Double,
	val goalUnit: String,
	val goalType: String = "distance",
	val timeOfDayMinutes: Int? = null,
	val alarmMode: String = "none",
	val reminderMinutesBefore: Int? = null,
	val pausedAtEpochMs: Long? = null,
	val archivedAtEpochMs: Long? = null,
	val createdAtEpochMs: Long,
)
