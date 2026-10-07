package app.lade.ui.theme

import androidx.compose.runtime.compositionLocalOf
import java.time.LocalDate

val LocalToday = compositionLocalOf { LocalDate.now() }