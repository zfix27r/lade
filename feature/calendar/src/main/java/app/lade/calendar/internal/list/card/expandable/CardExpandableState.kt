package app.lade.calendar.internal.list.card.expandable

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue

@Stable
internal class CardExpandableState(
    initial: Set<String> = emptySet(),
) {
    private var expandedKeys by mutableStateOf(initial)

    fun isExpanded(key: CardExpandableKey): Boolean =
        key.asString in expandedKeys

    fun toggle(key: CardExpandableKey) {
        expandedKeys = if (key.asString in expandedKeys) {
            expandedKeys - key.asString
        } else {
            expandedKeys + key.asString
        }
    }

    fun collapse(key: CardExpandableKey) {
        if (key.asString in expandedKeys) {
            expandedKeys = expandedKeys - key.asString
        }
    }

    fun expandAll(keys: Collection<CardExpandableKey>) {
        expandedKeys = expandedKeys + keys.map { it.asString }
    }

    fun collapseAll() {
        expandedKeys = emptySet()
    }

    val currentKeys: Set<String> get() = expandedKeys

    companion object {
        val Saver: Saver<CardExpandableState, List<String>> = Saver(
            save = { it.currentKeys.toList() },
            restore = { CardExpandableState(it.toSet()) },
        )
    }
}

