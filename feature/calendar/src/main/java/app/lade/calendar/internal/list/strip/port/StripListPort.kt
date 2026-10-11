package app.lade.calendar.internal.list.strip.port

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

internal interface StripListPort {
    val connection: NestedScrollConnection
}