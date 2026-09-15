package app.lade.agendastore.log

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.lade.agendastore.entry.EntryEntity

@Entity(
	tableName = "logs",
	foreignKeys = [
		ForeignKey(
			entity = EntryEntity::class,
			parentColumns = ["id"],
			childColumns = ["entryId"],
			onDelete = ForeignKey.CASCADE,
		),
	],
	indices = [
		Index("entryId"),
		Index("epochDay"),
		Index("goalId"),
		Index(value = ["entryId", "epochDay"]),
		Index(value = ["goalId", "epochDay"]),
	],
)
data class LogEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val entryId: Long,
	val epochDay: Long,
	val goalId: Long? = null,
	val name: String? = null,
	val unit: String? = null,
	val plannedAmount: Int? = null,
	val plannedRepeat: Int? = null,
	val plannedWeight: Double? = null,
	val actualAmount: Int? = null,
	val actualRepeat: Int? = null,
	val actualWeight: Double? = null,
	val origin: String = "unknown",
	val createdAtEpochMs: Long,
)