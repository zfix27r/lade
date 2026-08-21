package app.lade.categories.data

import app.lade.categories.domain.CategoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CategoriesDataModule {
	@Binds
	@Singleton
	abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
}
