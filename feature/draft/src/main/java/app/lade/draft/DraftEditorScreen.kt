package app.lade.draft

import androidx.compose.runtime.Composable
import app.lade.draft.internal.editor.DraftEditorScreenInternal

@Composable
fun DraftEditorScreen(
    onBack: () -> Unit,
) {
    DraftEditorScreenInternal(onBack = onBack)
}