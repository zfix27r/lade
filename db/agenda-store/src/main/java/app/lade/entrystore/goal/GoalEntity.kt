package app.lade.entrystore.goal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.lade.entrystore.entry.EntryEntity

@Entity(
    tableName = "goals",
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
        Index("templateId"),
    ],
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryId: Long,
    val templateId: Long? = null,
    val title: String,
    val unit: String,
    val amount: Int? = null,
    val repeat: Int? = null,
    val weight: Double? = null,
    val archivedAtEpochMs: Long? = null,
)