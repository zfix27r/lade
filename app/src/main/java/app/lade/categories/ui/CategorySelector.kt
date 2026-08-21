package app.lade.categories.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.lade.R
import app.lade.categories.domain.model.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
	categories: List<Category>,
	selectedId: Long?,
	onSelected: (Long) -> Unit,
	modifier: Modifier = Modifier,
	label: String? = null,
) {
	var expanded by remember { mutableStateOf(false) }
	val selected = categories.find { it.id == selectedId }
	val placeholder = stringResource(R.string.action_select)
	val fieldLabel = label ?: stringResource(R.string.category_selector_label)
	ExposedDropdownMenuBox(
		expanded = expanded,
		onExpandedChange = { expanded = it },
		modifier = modifier.fillMaxWidth(),
	) {
		OutlinedTextField(
			value = selected?.title.orEmpty().ifEmpty { placeholder },
			onValueChange = {},
			readOnly = true,
			label = { Text(fieldLabel) },
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
			modifier = Modifier
				.menuAnchor(MenuAnchorType.PrimaryNotEditable)
				.fillMaxWidth(),
		)
		ExposedDropdownMenu(
			expanded = expanded,
			onDismissRequest = { expanded = false },
		) {
			categories.forEach { category ->
				DropdownMenuItem(
					text = { Text(category.title) },
					onClick = {
						onSelected(category.id)
						expanded = false
					},
				)
			}
		}
	}
}
