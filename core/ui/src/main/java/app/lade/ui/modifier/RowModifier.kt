package app.lade.ui.modifier

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import app.lade.ui.theme.Radius
import app.lade.ui.theme.Row
import app.lade.ui.theme.Spacing

fun Modifier.rowSm(): Modifier = this
    .fillMaxWidth()
    .heightIn(min = Row.sm)
    .clip(RoundedCornerShape(Radius.sm))

fun Modifier.rowMd(): Modifier = this
    .fillMaxWidth()
    .heightIn(min = Row.md)
    .clip(RoundedCornerShape(Radius.sm))

fun Modifier.rowLg(): Modifier = this
    .fillMaxWidth()
    .heightIn(min = Row.lg)
    .clip(RoundedCornerShape(Radius.sm))
