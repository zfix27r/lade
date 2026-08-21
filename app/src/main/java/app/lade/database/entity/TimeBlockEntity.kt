package app.lade.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "time_blocks",
	foreignKeys = [
		ForeignKey(
			entity = CategoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.RESTRICT,
		),
		ForeignKey(
			entity = TimeScheduleEntity::class,
			parentColumns = ["id"],
			childColumns = ["scheduleId"],
			onDelete = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("dateEpochDay"),
		Index("categoryId"),
		Index("scheduleId"),
		Index(value = ["scheduleId", "dateEpochDay"], unique = true),
	],
)
data class TimeBlockEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val dateEpochDay: Long,
	/** Minutes from midnight. Same calendar day; end > start. */
	val startTimeMinutes: Int,
	val endTimeMinutes: Int,
	val categoryId: Long,
	val title: String? = null,
	/** schedule | manual | health | calendar */
	val source: String,
	val scheduleId: Long? = null,
	val habitHistoryId: Long? = null,
	val healthSampleId: Long? = null,
	val calendarEventId: String? = null,
	val locked: Boolean = false,
)
