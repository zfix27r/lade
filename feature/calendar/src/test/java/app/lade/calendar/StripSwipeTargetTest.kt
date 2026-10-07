package app.lade.calendar

import app.lade.calendar.internal.list.strip.data.stripSwipeTarget
import junit.framework.TestCase.assertEquals
import org.junit.Test

class StripSwipeTargetTest {

    private val width = 700f
    private val threshold = 100f

    @Test
    fun `strong swipe right returns positive width`() {
        val result = stripSwipeTarget(
            offsetX = 0f,
            totalX = 150f,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(width, result)
    }

    @Test
    fun `strong swipe left returns negative width`() {
        val result = stripSwipeTarget(
            offsetX = 0f,
            totalX = -150f,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(-width, result)
    }

    @Test
    fun `weak swipe but offset past half right returns positive width`() {
        val result = stripSwipeTarget(
            offsetX = 400f,
            totalX = 50f,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(width, result)
    }

    @Test
    fun `weak swipe but offset past half left returns negative width`() {
        val result = stripSwipeTarget(
            offsetX = -400f,
            totalX = -50f,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(-width, result)
    }

    @Test
    fun `weak swipe with offset in middle returns zero`() {
        val result = stripSwipeTarget(
            offsetX = 100f,
            totalX = 30f,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(0f, result)
    }

    @Test
    fun `total equal to threshold does not trigger swipe`() {
        val result = stripSwipeTarget(
            offsetX = 0f,
            totalX = threshold,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(0f, result)
    }

    @Test
    fun `offset equal to half width does not trigger swipe`() {
        val result = stripSwipeTarget(
            offsetX = width / 2f,
            totalX = 0f,
            thresholdPx = threshold,
            widthPx = width,
        )
        assertEquals(0f, result)
    }
}