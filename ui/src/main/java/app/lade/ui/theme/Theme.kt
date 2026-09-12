package app.lade.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import app.lade.resources.R

@Composable
fun LadeTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit,
) {
	val navy = colorResource(R.color.lade_navy)
	val mist = colorResource(R.color.lade_mist)
	val accent = colorResource(R.color.lade_accent)
	val white = colorResource(R.color.lade_white)
	val darkBackground = colorResource(R.color.lade_dark_background)
	val darkSurface = colorResource(R.color.lade_dark_surface)

	val colorScheme = if (darkTheme) {
		darkColorScheme(
			primary = accent,
			onPrimary = white,
			secondary = mist,
			onSecondary = navy,
			background = darkBackground,
			onBackground = mist,
			surface = darkSurface,
			onSurface = mist,
		)
	} else {
		lightColorScheme(
			primary = navy,
			onPrimary = white,
			secondary = accent,
			onSecondary = white,
			background = mist,
			onBackground = navy,
			surface = white,
			onSurface = navy,
		)
	}

	MaterialTheme(
		colorScheme = colorScheme,
		content = content,
	)
}
