package app.lade.calendar.ui.component.year

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import app.lade.calendardata.api.CalendarCardModel
import app.lade.resources.R
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun CalendarYearGrid(
    year: Int,
    entries: List<CalendarCardModel>,
    onOpenMonth: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = remember { Locale.getDefault() }
    val today = remember { LocalDate.now() }
    val months = remember(year) { Month.entries.map { YearMonth.of(year, it) } }
    val monthsWithEntries = entries.map { YearMonth.from(it.date) }.toSet()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = dimensionResource(R.dimen.spacing_sm)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        ) {
            items(months, key = { it.toString() }) { yearMonth ->
                CalendarYearCell(
                    label = yearMonth.month
                        .getDisplayName(TextStyle.FULL_STANDALONE, locale)
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(locale) else it.toString()
                        },
                    isCurrent = yearMonth == YearMonth.from(today),
                    hasEntries = yearMonth in monthsWithEntries,
                    onClick = { onOpenMonth(yearMonth) },
                )
            }
        }
    }
}