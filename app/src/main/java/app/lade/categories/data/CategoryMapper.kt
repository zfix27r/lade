package app.lade.categories.data

import app.lade.categories.domain.model.Category
import app.lade.database.entity.CategoryEntity

fun CategoryEntity.toDomain() = Category(
	id = id,
	key = key,
	title = title,
	color = color,
	archivedAtEpochMs = archivedAtEpochMs,
)

fun Category.toEntity() = CategoryEntity(
	id = id,
	key = key,
	title = title,
	color = color,
	archivedAtEpochMs = archivedAtEpochMs,
)
