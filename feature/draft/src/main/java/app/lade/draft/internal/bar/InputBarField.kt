package app.lade.draft.internal.bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
internal fun InputBarField(
    state: InputBarState,
    placeholder: String,
    maxLines: Int,
    onSubmit: () -> Unit,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = state.text,
        onValueChange = { value ->
            state.onTextChange(value)
            onTextChange(value)
        },
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = state.keyboardActions(onSubmit),
        modifier = modifier
            .fillMaxWidth()
            .then(state.focusModifier()),
        decorationBox = { inner ->
            Box {
                if (state.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
                inner()
            }
        },
    )
}