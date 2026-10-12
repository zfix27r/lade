package app.lade.calendar.internal.card.expandable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
internal fun rememberCardExpandableState(): CardExpandableState =
    rememberSaveable(saver = CardExpandableState.Saver) {
        CardExpandableState()
    }