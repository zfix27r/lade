package app.lade.database.category

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.jvmErasure
import org.junit.Assert.assertEquals
import org.junit.Test

class CategoryEntityContractTest {

	@Test
	fun categoryEntity_frozenFields() {
		val fields = CategoryEntity::class.declaredMemberProperties.map { it.name }.toSet()
		assertEquals(
			setOf("id", "key", "title", "color", "archivedAtEpochMs"),
			fields,
		)
	}

	@Test
	fun categoryEntity_frozenTypes() {
		val types = CategoryEntity::class.declaredMemberProperties
			.associate { it.name to it.returnType.jvmErasure }
		assertEquals(
			mapOf(
				"id" to Long::class,
				"key" to String::class,
				"title" to String::class,
				"color" to String::class,
				"archivedAtEpochMs" to Long::class,
			),
			types,
		)
	}

	@Test
	fun categoryEntity_frozenNullability() {
		val nullability = CategoryEntity::class.declaredMemberProperties
			.associate { it.name to it.returnType.isMarkedNullable }
		assertEquals(
			mapOf(
				"id" to false,
				"key" to true,
				"title" to false,
				"color" to false,
				"archivedAtEpochMs" to true,
			),
			nullability,
		)
	}
}
