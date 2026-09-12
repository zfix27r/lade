package app.lade.ui.components.indicators

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import app.lade.resources.R
import app.lade.ui.tokens.colors.categoryColor

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