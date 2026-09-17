package app.lade.calendar.ui.component.year

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import app.lade.agenda.api.agenda.AgendaModel
import app.lade.calendar.domain.CalendarDateMode
import app.lade.calendar.ui.component.swipe.CalendarDateSwipe
import app.lade.resources.R
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarYearGrid(
	year: Int,
	entries: List<AgendaModel>,
	onSwipe: (CalendarDateMode) -> Unit,
	onOpenMonth: (YearMonth) -> Unit,
	modifier: Modifier = Modifier,
) {
	val locale = remember { Locale.getDefault() }
	val today = remember { LocalDate.now() }
	val months = remember(year) { Month.entries.map { YearMonth.of(year, it) } }
	val monthsWithEntries = entries.map { YearMonth.from(it.date) }.toSet()

	CalendarDateSwipe(
		onSwipe = onSwipe,
		modifier = modifier.fillMaxSize(),
	) {
		Column(
			modifier = Modifier
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
					YearMonthCell(
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
}

@Composable
private fun YearMonthCell(
	label: String,
	isCurrent: Boolean,
	hasEntries: Boolean,
	onClick: () -> Unit,
) {
	val bg = when {
		isCurrent -> MaterialTheme.colorScheme.primaryContainer
		hasEntries -> MaterialTheme.colorScheme.surfaceVariant
		else -> MaterialTheme.colorScheme.surface
	}
	val fg = when {
		isCurrent -> MaterialTheme.colorScheme.onPrimaryContainer
		else -> MaterialTheme.colorScheme.onSurfaceVariant
	}
	Box(
		modifier = Modifier
			.aspectRatio(1.2f)
			.clip(RoundedCornerShape(dimensionResource(R.dimen.spacing_md)))
			.background(bg)
			.clickable(onClick = onClick)
			.padding(dimensionResource(R.dimen.spacing_sm)),
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = label,
			style = MaterialTheme.typography.titleSmall,
			color = fg,
			textAlign = TextAlign.Center,
		)
	}
}