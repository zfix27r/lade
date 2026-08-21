package app.lade.database.seed

import android.content.Context
import app.lade.R
import app.lade.database.dao.CategoryDao
import app.lade.database.entity.CategoryEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorySeeder @Inject constructor(
	private val categoryDao: CategoryDao,
	@ApplicationContext private val context: Context,
) {
	suspend fun seedIfEmpty() {
		if (categoryDao.count() > 0) return
		categoryDao.insertAll(
			SEED.map { (key, titleRes, color) ->
				CategoryEntity(
					key = key,
					title = context.getString(titleRes),
					color = color,
				)
			},
		)
	}

	companion object {
		private val SEED: List<Triple<String, Int, String>> = listOf(
			Triple("sleep", R.string.seed_category_sleep, "violet"),
			Triple("work", R.string.seed_category_work, "blue"),
			Triple("lunch", R.string.seed_category_lunch, "amber"),
			Triple("meal", R.string.seed_category_meal, "amber"),
			Triple("commute", R.string.seed_category_commute, "stone"),
			Triple("sport", R.string.seed_category_sport, "green"),
			Triple("run", R.string.seed_category_run, "green"),
			Triple("walk", R.string.seed_category_walk, "green"),
			Triple("health", R.string.seed_category_health, "teal"),
			Triple("study", R.string.seed_category_study, "sky"),
			Triple("home", R.string.seed_category_home, "orange"),
			Triple("family", R.string.seed_category_family, "pink"),
			Triple("leisure", R.string.seed_category_leisure, "red"),
			Triple("care", R.string.seed_category_care, "slate"),
			Triple("other", R.string.seed_category_other, "stone"),
		)
	}
}
