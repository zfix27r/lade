package app.lade.categories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.lade.resources.R
import app.lade.categories.domain.model.CategoryColors
import app.lade.ui.tokens.colors.categoryColor

/**
 * Preset color circles (~20) + trailing gradient chip for custom `#RRGGBB`.
 */
@Composable
fun CategoryColorPicker(
	selected: String,
	onSelected: (String) -> Unit,
	modifier: Modifier = Modifier,
) {
	var showCustom by remember { mutableStateOf(false) }
	var hexDraft by remember(selected) {
		mutableStateOf(if (CategoryColors.isCustomHex(selected)) selected else "#16A34A")
	}
	val customLabel = stringResource(R.string.category_color_custom)

	Row(
		modifier = modifier
			.fillMaxWidth()
			.horizontalScroll(rememberScrollState()),
		horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
		verticalAlignment = Alignment.CenterVertically,
	) {
		CategoryColors.ALL.forEach { key ->
			val color = categoryColor(key)
			val isSelected = selected == key
			Box(
				modifier = Modifier
					.size(dimensionResource(R.dimen.category_color_swatch))
					.clip(CircleShape)
					.background(color)
					.then(
						if (isSelected) {
							Modifier.border(
								2.dp,
								MaterialTheme.colorScheme.onSurface,
								CircleShape,
							)
						} else {
							Modifier
						},
					)
					.clickable { onSelected(key) }
					.semantics { contentDescription = key },
			)
		}
		val gradient = Brush.linearGradient(
			listOf(
				Color(0xFFE11D48),
				Color(0xFFCA8A04),
				Color(0xFF16A34A),
				Color(0xFF2563EB),
				Color(0xFF7C3AED),
			),
		)
		val customSelected = CategoryColors.isCustomHex(selected)
		Box(
			modifier = Modifier
				.size(dimensionResource(R.dimen.category_color_swatch))
				.clip(CircleShape)
				.background(gradient)
				.then(
					if (customSelected) {
						Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
					} else {
						Modifier
					},
				)
				.clickable { showCustom = true }
				.semantics { contentDescription = customLabel },
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = stringResource(R.string.category_color_custom_glyph),
				style = MaterialTheme.typography.labelLarge,
				color = Color.White,
			)
		}
	}

	if (showCustom) {
		AlertDialog(
			onDismissRequest = { showCustom = false },
			title = { Text(customLabel) },
			text = {
				Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md))) {
					OutlinedTextField(
						value = hexDraft,
						onValueChange = { hexDraft = it.trim() },
						label = { Text(stringResource(R.string.category_color_hex_hint)) },
						singleLine = true,
						modifier = Modifier.fillMaxWidth(),
					)
					if (CategoryColors.isCustomHex(hexDraft)) {
						Box(
							modifier = Modifier
								.size(dimensionResource(R.dimen.category_color_swatch))
								.clip(CircleShape)
								.background(categoryColor(hexDraft)),
						)
					}
				}
			},
			confirmButton = {
				TextButton(
					onClick = {
						val normalized = hexDraft.uppercase()
						if (CategoryColors.isCustomHex(normalized)) {
							onSelected(normalized)
							showCustom = false
						}
					},
					enabled = CategoryColors.isCustomHex(hexDraft.uppercase()),
				) {
					Text(stringResource(R.string.action_ok))
				}
			},
			dismissButton = {
				TextButton(onClick = { showCustom = false }) {
					Text(stringResource(R.string.action_cancel))
				}
			},
		)
	}
}
