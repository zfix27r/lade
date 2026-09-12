package app.lade.categories.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lade.resources.R
import app.lade.categories.domain.model.Category
import app.lade.categories.domain.model.CategoryColors
import app.lade.ui.tokens.colors.categoryColor

/**
 * Flow of circular category chips (letter on color). Trailing «А» opens inline create.
 * [showLabel] off by default when the parent card already titles the section.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySelector(
	categories: List<Category>,
	selectedId: Long?,
	onSelected: (Long) -> Unit,
	modifier: Modifier = Modifier,
	showLabel: Boolean = false,
	label: String? = null,
	onCreateCategory: ((title: String, color: String) -> Unit)? = null,
) {
	var creating by remember { mutableStateOf(false) }
	var draftTitle by remember { mutableStateOf("") }
	var draftColor by remember { mutableStateOf(CategoryColors.ALL[9]) } // green
	val fieldLabel = label ?: stringResource(R.string.category_selector_label)
	val textModeLabel = stringResource(R.string.category_selector_text_mode)
	val textModeCd = stringResource(R.string.category_selector_text_mode_cd)
	val selectedCategory = categories.find { it.id == selectedId }

	Column(
		modifier = modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
	) {
		if (showLabel) {
			Text(
				text = fieldLabel,
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
		if (!creating) {
			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
				verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
			) {
				categories.forEach { category ->
					val letter = categoryLetter(category)
					val selected = category.id == selectedId
					CategoryLetterChip(
						letter = letter,
						background = categoryColor(category.color),
						selected = selected,
						contentDescription = category.title,
						onClick = { onSelected(category.id) },
					)
				}
				if (onCreateCategory != null) {
					CategoryLetterChip(
						letter = textModeLabel,
						background = MaterialTheme.colorScheme.surfaceVariant,
						foreground = MaterialTheme.colorScheme.onSurfaceVariant,
						selected = false,
						contentDescription = textModeCd,
						onClick = {
							draftTitle = ""
							draftColor = CategoryColors.ALL[9]
							creating = true
						},
						outlined = true,
					)
				}
			}
			if (selectedCategory != null) {
				Text(
					text = selectedCategory.title,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurface,
				)
			}
		} else {
			OutlinedTextField(
				value = draftTitle,
				onValueChange = { draftTitle = it },
				label = { Text(stringResource(R.string.category_field_title)) },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true,
			)
			CategoryColorPicker(
				selected = draftColor,
				onSelected = { draftColor = it },
			)
			Row(
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
			) {
				TextButton(
					onClick = { creating = false },
				) {
					Text(stringResource(R.string.action_cancel))
				}
				TextButton(
					onClick = {
						val title = draftTitle.trim()
						if (title.isNotEmpty() && onCreateCategory != null) {
							onCreateCategory(title, draftColor)
							creating = false
						}
					},
					enabled = draftTitle.isNotBlank(),
				) {
					Text(stringResource(R.string.action_save))
				}
			}
		}
	}
}

@Composable
private fun CategoryLetterChip(
	letter: String,
	background: Color,
	selected: Boolean,
	contentDescription: String,
	onClick: () -> Unit,
	foreground: Color = Color.White,
	outlined: Boolean = false,
) {
	val size = dimensionResource(R.dimen.category_selector_chip)
	Box(
		modifier = Modifier
			.size(size)
			.clip(CircleShape)
			.background(background)
			.then(
				when {
					selected -> Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
					outlined -> Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
					else -> Modifier
				},
			)
			.clickable(onClick = onClick)
			.semantics { this.contentDescription = contentDescription },
		contentAlignment = Alignment.Center,
	) {
		Text(
			text = letter,
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.SemiBold,
			color = foreground,
		)
	}
}

private fun categoryLetter(category: Category): String {
	val fromTitle = category.title.trim().firstOrNull()?.uppercaseChar()
	if (fromTitle != null && !fromTitle.isWhitespace()) return fromTitle.toString()
	val fromKey = category.key?.trim()?.firstOrNull()?.uppercaseChar()
	return fromKey?.toString() ?: "?"
}
