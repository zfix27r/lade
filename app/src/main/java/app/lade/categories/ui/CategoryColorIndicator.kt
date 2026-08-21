package app.lade.categories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import app.lade.R

@Composable
fun CategoryColorIndicator(
	colorKey: String,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.size(dimensionResource(R.dimen.category_color_indicator))
			.clip(CircleShape)
			.background(categoryColor(colorKey)),
	)
}

@Composable
fun categoryColor(colorKey: String): Color {
	val resId = when (colorKey) {
		"slate" -> R.color.category_slate
		"stone" -> R.color.category_stone
		"red" -> R.color.category_red
		"orange" -> R.color.category_orange
		"amber" -> R.color.category_amber
		"green" -> R.color.category_green
		"teal" -> R.color.category_teal
		"sky" -> R.color.category_sky
		"blue" -> R.color.category_blue
		"violet" -> R.color.category_violet
		"pink" -> R.color.category_pink
		else -> R.color.category_slate
	}
	return colorResource(resId)
}
