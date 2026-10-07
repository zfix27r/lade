package app.lade.calendar

import androidx.compose.ui.unit.dp
import java.time.LocalDate
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class StripMetricsTest {

    private val weeks: List<List<LocalDate>> = (0 until 6).map { w ->
        (0 until 7).map { d -> LocalDate.of(2026, 10, 1).plusDays((w * 7 + d).toLong()) }
    }

    private fun metrics(
        activeWeekIndex: Int = 0,
        rowHeight: Int = 40,
    ): StripMetrics = StripMetrics(
        weeks = weeks,
        activeWeekIndex = activeWeekIndex,
        rowHeight = rowHeight.dp,
        monthHeight = (rowHeight * 6).dp,
        collapsedHeight = rowHeight.dp,
    )

    // isMonthMode

    @Test
    fun `progress zero is not month mode`() {
        assertFalse(metrics().isMonthMode(0f))
    }

    @Test
    fun `progress below half is not month mode`() {
        assertFalse(metrics().isMonthMode(0.49f))
    }

    @Test
    fun `progress exactly half is month mode`() {
        assertTrue(metrics().isMonthMode(0.5f))
    }

    @Test
    fun `progress one is month mode`() {
        assertTrue(metrics().isMonthMode(1f))
    }

    // columnOffsetY

    @Test
    fun `column offset at progress one is zero`() {
        assertEquals(0.dp, metrics(activeWeekIndex = 3).columnOffsetY(1f))
    }

    @Test
    fun `column offset at progress zero shifts up by active week`() {
        assertEquals(40.dp * 3 * -1, metrics(activeWeekIndex = 3).columnOffsetY(0f))
    }

    @Test
    fun `column offset at progress half shifts half of active week`() {
        assertEquals(40.dp * 3 * -0.5f, metrics(activeWeekIndex = 3).columnOffsetY(0.5f))
    }

    @Test
    fun `column offset is zero when active week is first`() {
        val m = metrics(activeWeekIndex = 0)
        assertEquals(0f, m.columnOffsetY(0f).value, 0.001f)
        assertEquals(0f, m.columnOffsetY(1f).value, 0.001f)
    }

    // listTopOffset

    @Test
    fun `list top offset at progress zero equals collapsed height`() {
        assertEquals(40.dp, metrics(rowHeight = 40).listTopOffset(0f))
    }

    @Test
    fun `list top offset at progress one equals month height`() {
        assertEquals(240.dp, metrics(rowHeight = 40).listTopOffset(1f))
    }

    @Test
    fun `list top offset at progress half is between collapsed and month`() {
        assertEquals(140.dp, metrics(rowHeight = 40).listTopOffset(0.5f))
    }

    // dateAt

    @Test
    fun `date at first cell returns first day of first week`() {
        val result = metrics().dateAt(
            x = 0f,
            y = 0f,
            widthPx = 700,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertEquals(weeks[0][0], result)
    }

    @Test
    fun `date at last column of first row returns seventh day`() {
        val result = metrics().dateAt(
            x = 699f,
            y = 0f,
            widthPx = 700,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertEquals(weeks[0][6], result)
    }

    @Test
    fun `date at second row returns first day of second week`() {
        val result = metrics().dateAt(
            x = 0f,
            y = 40f,
            widthPx = 700,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertEquals(weeks[1][0], result)
    }

    @Test
    fun `date respects column offset`() {
        val result = metrics().dateAt(
            x = 0f,
            y = 80f,
            widthPx = 700,
            columnOffsetPx = 80f,
            rowHeightPx = 40f,
        )
        assertEquals(weeks[0][0], result)
    }

    @Test
    fun `date returns null when width is zero`() {
        val result = metrics().dateAt(
            x = 0f,
            y = 0f,
            widthPx = 0,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertNull(result)
    }

    @Test
    fun `date returns null when row is above grid`() {
        val result = metrics().dateAt(
            x = 0f,
            y = -100f,
            widthPx = 700,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertNull(result)
    }

    @Test
    fun `date returns null when row is below grid`() {
        val result = metrics().dateAt(
            x = 0f,
            y = 1000f,
            widthPx = 700,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertNull(result)
    }

    @Test
    fun `date clamps column when x beyond right edge`() {
        val result = metrics().dateAt(
            x = 10_000f,
            y = 0f,
            widthPx = 700,
            columnOffsetPx = 0f,
            rowHeightPx = 40f,
        )
        assertEquals(weeks[0][6], result)
    }
}