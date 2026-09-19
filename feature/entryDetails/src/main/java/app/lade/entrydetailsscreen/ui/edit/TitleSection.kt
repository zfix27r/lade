package app.lade.entrydetailsscreen.ui.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.lade.resources.R
import app.lade.ui.components.inputs.TitleTextField
import app.lade.ui.components.layout.EditSectionCard

@Composable
fun TitleSection(
    title: String,
    onTitleChange: (String) -> Unit,
    enabled: Boolean,
) {
    EditSectionCard(title = stringResource(R.string.edit_section_title)) {
        TitleTextField(
            value = title,
            onValueChange = onTitleChange,
            enabled = enabled,
        )
    }
}