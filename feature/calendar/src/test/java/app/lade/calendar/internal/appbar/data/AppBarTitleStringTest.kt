package app.lade.calendar.internal.appbar.data

import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.calendar.internal.list.strip.data.StripState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AppBarTitleStateTest {

    private val locale = Locale.forLanguageTag("ru-RU")
    private val monthFmt = DateTimeFormatter.ofPattern("LLL", locale)
    private val today = LocalDate.of(2026, 10, 6)

    private fun state(
        mode: CalendarMode,
        currentDate: LocalDate,
        stripState: StripState? = null,
    ) = appBarTitleString(
        mode = mode,
        currentDate = currentDate,
        stripState = stripState,
        today = today,
        monthFmt = monthFmt,
    )

    @Test
    fun `list uses anchor when phase current`() {
        val s = state(
            mode = CalendarMode.LIST,
            currentDate = LocalDate.of(2026, 10, 6),
            stripState = StripState(
                date = LocalDate.of(2026, 11, 3),
                widthPx = 1000,
                offsetX = 0f,
            ),
        )
        assertEquals("нояб.", s.text)
    }

    @Test
    fun `list uses next when phase next`() {
        val s = state(
            mode = CalendarMode.LIST,
            currentDate = LocalDate.of(2026, 10, 6),
            stripState = StripState(
                date = LocalDate.of(2026, 10, 6),
                widthPx = 1000,
                offsetX = -300f,
            ),
        )
        assertEquals("нояб.", s.text)
    }

    @Test
    fun `list uses prev when phase prev`() {
        val s = state(
            mode = CalendarMode.LIST,
            currentDate = LocalDate.of(2026, 10, 6),
            stripState = StripState(
                date = LocalDate.of(2026, 10, 6),
                widthPx = 1000,
                offsetX = 300f,
            ),
        )
        assertEquals("сент.", s.text)
    }

    @Test
    fun `year mode returns year`() {
        val s = state(
            mode = CalendarMode.YEAR,
            currentDate = LocalDate.of(2026, 10, 6),
        )
        assertEquals("2026", s.text)
    }

    @Test
    fun `month with different year shows year`() {
        val s = state(
            mode = CalendarMode.LIST,
            currentDate = LocalDate.of(2026, 10, 6),
            stripState = StripState(
                date = LocalDate.of(2027, 3, 15),
                widthPx = 1000,
                offsetX = 0f,
            ),
        )
        assertEquals("мар. 2027", s.text)
    }
}