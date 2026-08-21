package app.lade.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "habit_history",
	foreignKeys = [
		ForeignKey(
			entity = HabitEntity::class,
			parentColumns = ["id"],
			childColumns = ["habitId"],
			onDelete = ForeignKey.CASCADE,
		),
		ForeignKey(
			entity = CategoryEntity::class,
			parentColumns = ["id"],
			childColumns = ["categoryId"],
			onDelete = ForeignKey.RESTRICT,
		),
	],
	indices = [
		Index(value = ["habitId", "dateEpochDay"], unique = true),
		Index("categoryId"),
	],
)
data class HabitHistoryEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val habitId: Long,
	val dateEpochDay: Long,
	val title: String,
	val categoryId: Long,
	val goalType: String,
	val goalValue: Double,
	val goalUnit: String,
	val timeOfDayMinutes: Int? = null,
	val result: String? = null,
	val actualValue: Double? = null,
	val notedAtEpochMs: Long? = null,
	val source: String? = null,
	val timeBlockId: Long? = null,
)
