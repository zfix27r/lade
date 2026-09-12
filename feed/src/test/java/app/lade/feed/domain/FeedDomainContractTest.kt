package app.lade.feed.domain

import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import org.junit.Assert.assertEquals
import org.junit.Test

class FeedDomainContractTest {

	@Test
	fun feedEntry_frozenFields() {
		val fields = FeedEntry::class.declaredMemberProperties.map { it.name }.toSet()
		assertEquals(setOf("id", "kind", "title", "result"), fields)
	}

	@Test
	fun feedState_frozenFields() {
		val fields = FeedState::class.declaredMemberProperties.map { it.name }.toSet()
		assertEquals(setOf("dateEpochDay", "entries"), fields)
	}

	@Test
	fun feedRepository_frozenApi() {
		val methods = FeedRepository::class.declaredMemberFunctions.map { it.name }.toSet()
		assertEquals(setOf("observeState"), methods)
	}
}
