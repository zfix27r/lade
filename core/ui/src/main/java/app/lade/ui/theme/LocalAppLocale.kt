package app.lade.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

val LocalAppLocale = staticCompositionLocalOf { Locale.getDefault() }