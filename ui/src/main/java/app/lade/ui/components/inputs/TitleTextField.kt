package app.lade.ui.components.inputs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import app.lade.resources.R

@Composable
fun TitleTextField(
	value: String,
	onValueChange: (String) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
) {
	OutlinedTextField(
		value = value,
		onValueChange = onValueChange,
		modifier = modifier.fillMaxWidth(),
		singleLine = true,
		enabled = enabled,
		keyboardOptions = KeyboardOptions(
			capitalization = KeyboardCapitalization.Sentences,
		),
		trailingIcon = {
			if (enabled && value.isNotEmpty()) {
				IconButton(onClick = { onValueChange("") }) {
					Icon(
						Icons.Default.Close,
						contentDescription = stringResource(R.string.action_clear_field),
					)
				}
			}
		},
	)
}
