package app.lade.categories.data

import app.lade.categories.domain.CategoryRepository
import app.lade.categories.domain.model.Category
import app.lade.database.dao.CategoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
	private val dao: CategoryDao,
) : CategoryRepository {
	override fun observeActive(): Flow<List<Category>> =
		dao.observeActive().map { list -> list.map { it.toDomain() } }

	override suspend fun getById(id: Long): Category? =
		dao.getById(id)?.toDomain()

	override suspend fun save(category: Category): Long {
		return if (category.id == 0L) {
			dao.insert(category.toEntity().copy(id = 0))
		} else {
			dao.update(category.toEntity())
			category.id
		}
	}

	override suspend fun archive(id: Long) {
		dao.archive(id, System.currentTimeMillis())
	}
}
