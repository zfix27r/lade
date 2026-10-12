package app.lade.ui.modifier

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import app.lade.ui.theme.Radius

fun Modifier.cardSm(): Modifier = this
    .fillMaxWidth()
    .clip(RoundedCornerShape(Radius.sm))

fun Modifier.cardMd(): Modifier = this
    .fillMaxWidth()
    .clip(RoundedCornerShape(Radius.md))

fun Modifier.cardLg(): Modifier = this
    .fillMaxWidth()
    .clip(RoundedCornerShape(Radius.lg))