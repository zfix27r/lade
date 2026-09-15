package app.lade.categories.domain

import app.lade.categories.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
	fun observeActive(): Flow<List<Category>>
	suspend fun getById(id: Long): Category?
	suspend fun save(category: Category): Long
	suspend fun archive(id: Long)
}
