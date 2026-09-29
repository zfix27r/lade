package app.lade.draft.internal.chat.chip.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import app.lade.ui.theme.IconSizes

@Composable
internal fun ChipTrailingIcon(
    tint: Color,
    onRemove: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    if (onRemove == null) return
    Icon(
        imageVector = Icons.Outlined.Close,
        contentDescription = null,
        tint = tint,
        modifier = modifier
            .size(IconSizes.sm)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onRemove),
    )
}