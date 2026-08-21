package app.lade.habits.ui.mark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.habits.domain.HabitDayMarkKind
import app.lade.habits.domain.HabitStreakPreview

@Composable
fun HabitStreakPreviewRow(
	preview: HabitStreakPreview,
	modifier: Modifier = Modifier,
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
	) {
		Text(
			text = stringResource(R.string.habit_streak_format, preview.currentStreak),
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
		)
		Row(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs))) {
			preview.last7.forEach { kind ->
				Box(
					modifier = Modifier
						.size(dimensionResource(R.dimen.calendar_marker_dot))
						.clip(CircleShape)
						.background(last7Color(kind)),
				)
			}
		}
	}
}

@Composable
private fun last7Color(kind: HabitDayMarkKind): Color = when (kind) {
	HabitDayMarkKind.DONE -> MaterialTheme.colorScheme.primary
	HabitDayMarkKind.SKIPPED -> MaterialTheme.colorScheme.secondary
	HabitDayMarkKind.MISSED -> MaterialTheme.colorScheme.error
	HabitDayMarkKind.PLANNED -> MaterialTheme.colorScheme.outlineVariant
	HabitDayMarkKind.NONE -> MaterialTheme.colorScheme.surfaceVariant
}
