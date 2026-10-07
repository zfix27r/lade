package app.lade.calendar

import app.lade.calendar.internal.list.data.ListGoalsVisibility
import app.lade.calendardata.api.CalendarGoalModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ListGoalsVisibilityTest {

    private fun goal(id: Long): CalendarGoalModel = CalendarGoalModel(
        id = id,
        title = "g$id",
        description = "",
        isDone = false,
        actualAmount = null,
        plannedAmount = null,
    )

    private fun goals(count: Int): List<CalendarGoalModel> =
        (1..count).map { goal(it.toLong()) }

    @Test
    fun `empty pending yields empty visible and zero hidden`() {
        val result = ListGoalsVisibility.of(
            pending = emptyList(),
            expanded = false,
            limit = 3,
        )
        assertTrue(result.visible.isEmpty())
        assertEquals(0, result.hiddenCount)
    }

    @Test
    fun `fewer than limit shows all not expanded`() {
        val pending = goals(2)
        val result = ListGoalsVisibility.of(
            pending = pending,
            expanded = false,
            limit = 3,
        )
        assertEquals(pending, result.visible)
        assertEquals(0, result.hiddenCount)
    }

    @Test
    fun `exactly limit shows all not expanded`() {
        val pending = goals(3)
        val result = ListGoalsVisibility.of(
            pending = pending,
            expanded = false,
            limit = 3,
        )
        assertEquals(pending, result.visible)
        assertEquals(0, result.hiddenCount)
    }

    @Test
    fun `more than limit shows only first limit not expanded`() {
        val pending = goals(5)
        val result = ListGoalsVisibility.of(
            pending = pending,
            expanded = false,
            limit = 3,
        )
        assertEquals(pending.take(3), result.visible)
        assertEquals(2, result.hiddenCount)
    }

    @Test
    fun `expanded shows all and hides nothing`() {
        val pending = goals(5)
        val result = ListGoalsVisibility.of(
            pending = pending,
            expanded = true,
            limit = 3,
        )
        assertEquals(pending, result.visible)
        assertEquals(0, result.hiddenCount)
    }

    @Test
    fun `limit one shows single goal`() {
        val pending = goals(3)
        val result = ListGoalsVisibility.of(
            pending = pending,
            expanded = false,
            limit = 1,
        )
        assertEquals(pending.take(1), result.visible)
        assertEquals(2, result.hiddenCount)
    }

    @Test
    fun `limit zero shows nothing not expanded`() {
        val pending = goals(3)
        val result = ListGoalsVisibility.of(
            pending = pending,
            expanded = false,
            limit = 0,
        )
        assertTrue(result.visible.isEmpty())
        assertEquals(3, result.hiddenCount)
    }

    @Test
    fun `expanded empty pending stays empty`() {
        val result = ListGoalsVisibility.of(
            pending = emptyList(),
            expanded = true,
            limit = 3,
        )
        assertTrue(result.visible.isEmpty())
        assertEquals(0, result.hiddenCount)
    }
}