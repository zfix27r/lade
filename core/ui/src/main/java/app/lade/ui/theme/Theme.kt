package app.lade.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.colorResource
import app.lade.ui.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LadeTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	content: @Composable () -> Unit,
) {
	val colorScheme = if (darkTheme) darkScheme() else lightScheme()
	val motionScale = rememberAnimationScale()

	CompositionLocalProvider(LocalMotionScale provides motionScale) {
		MaterialTheme(
			colorScheme = colorScheme,
			typography = LadeTypography,
			shapes = LadeShapes,
			motionScheme = MotionScheme.expressive(),
			content = content,
		)
	}
}

@Composable
private fun lightScheme() = lightColorScheme(
	primary = colorResource(R.color.lade_primary_light),
	onPrimary = colorResource(R.color.lade_on_primary_light),
	primaryContainer = colorResource(R.color.lade_primary_container_light),
	onPrimaryContainer = colorResource(R.color.lade_on_primary_container_light),

	secondary = colorResource(R.color.lade_secondary_light),
	onSecondary = colorResource(R.color.lade_on_secondary_light),
	secondaryContainer = colorResource(R.color.lade_secondary_container_light),
	onSecondaryContainer = colorResource(R.color.lade_on_secondary_container_light),

	tertiary = colorResource(R.color.lade_tertiary_light),
	onTertiary = colorResource(R.color.lade_on_tertiary_light),
	tertiaryContainer = colorResource(R.color.lade_tertiary_container_light),
	onTertiaryContainer = colorResource(R.color.lade_on_tertiary_container_light),

	error = colorResource(R.color.lade_error_light),
	onError = colorResource(R.color.lade_on_error_light),
	errorContainer = colorResource(R.color.lade_error_container_light),
	onErrorContainer = colorResource(R.color.lade_on_error_container_light),

	background = colorResource(R.color.lade_background_light),
	onBackground = colorResource(R.color.lade_on_background_light),
	surface = colorResource(R.color.lade_surface_light),
	onSurface = colorResource(R.color.lade_on_surface_light),
	surfaceVariant = colorResource(R.color.lade_surface_variant_light),
	onSurfaceVariant = colorResource(R.color.lade_on_surface_variant_light),

	outline = colorResource(R.color.lade_outline_light),
	outlineVariant = colorResource(R.color.lade_outline_variant_light),

	surfaceDim = colorResource(R.color.lade_surface_dim_light),
	surfaceBright = colorResource(R.color.lade_surface_bright_light),
	surfaceContainerLowest = colorResource(R.color.lade_surface_container_lowest_light),
	surfaceContainerLow = colorResource(R.color.lade_surface_container_low_light),
	surfaceContainer = colorResource(R.color.lade_surface_container_light),
	surfaceContainerHigh = colorResource(R.color.lade_surface_container_high_light),
	surfaceContainerHighest = colorResource(R.color.lade_surface_container_highest_light),

	inverseSurface = colorResource(R.color.lade_inverse_surface_light),
	inverseOnSurface = colorResource(R.color.lade_inverse_on_surface_light),
	inversePrimary = colorResource(R.color.lade_inverse_primary_light),

	scrim = colorResource(R.color.lade_scrim),
)

@Composable
private fun darkScheme() = darkColorScheme(
	primary = colorResource(R.color.lade_primary_dark),
	onPrimary = colorResource(R.color.lade_on_primary_dark),
	primaryContainer = colorResource(R.color.lade_primary_container_dark),
	onPrimaryContainer = colorResource(R.color.lade_on_primary_container_dark),

	secondary = colorResource(R.color.lade_secondary_dark),
	onSecondary = colorResource(R.color.lade_on_secondary_dark),
	secondaryContainer = colorResource(R.color.lade_secondary_container_dark),
	onSecondaryContainer = colorResource(R.color.lade_on_secondary_container_dark),

	tertiary = colorResource(R.color.lade_tertiary_dark),
	onTertiary = colorResource(R.color.lade_on_tertiary_dark),
	tertiaryContainer = colorResource(R.color.lade_tertiary_container_dark),
	onTertiaryContainer = colorResource(R.color.lade_on_tertiary_container_dark),

	error = colorResource(R.color.lade_error_dark),
	onError = colorResource(R.color.lade_on_error_dark),
	errorContainer = colorResource(R.color.lade_error_container_dark),
	onErrorContainer = colorResource(R.color.lade_on_error_container_dark),

	background = colorResource(R.color.lade_background_dark),
	onBackground = colorResource(R.color.lade_on_background_dark),
	surface = colorResource(R.color.lade_surface_dark),
	onSurface = colorResource(R.color.lade_on_surface_dark),
	surfaceVariant = colorResource(R.color.lade_surface_variant_dark),
	onSurfaceVariant = colorResource(R.color.lade_on_surface_variant_dark),

	outline = colorResource(R.color.lade_outline_dark),
	outlineVariant = colorResource(R.color.lade_outline_variant_dark),

	surfaceDim = colorResource(R.color.lade_surface_dim_dark),
	surfaceBright = colorResource(R.color.lade_surface_bright_dark),
	surfaceContainerLowest = colorResource(R.color.lade_surface_container_lowest_dark),
	surfaceContainerLow = colorResource(R.color.lade_surface_container_low_dark),
	surfaceContainer = colorResource(R.color.lade_surface_container_dark),
	surfaceContainerHigh = colorResource(R.color.lade_surface_container_high_dark),
	surfaceContainerHighest = colorResource(R.color.lade_surface_container_highest_dark),

	inverseSurface = colorResource(R.color.lade_inverse_surface_dark),
	inverseOnSurface = colorResource(R.color.lade_inverse_on_surface_dark),
	inversePrimary = colorResource(R.color.lade_inverse_primary_dark),

	scrim = colorResource(R.color.lade_scrim),
)