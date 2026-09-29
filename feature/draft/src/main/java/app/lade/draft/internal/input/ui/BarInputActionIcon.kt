package app.lade.draft.internal.input.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import app.lade.draft.internal.input.domain.BarInputAction

@Composable
internal fun BarInputAction.icon(): ImageVector = when (this) {
    BarInputAction.Commit -> Icons.Outlined.Done
    BarInputAction.Send -> Icons.AutoMirrored.Filled.Send
}