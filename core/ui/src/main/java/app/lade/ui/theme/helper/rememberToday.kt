package app.lade.ui.theme.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.Duration.between
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun rememberToday(): LocalDate {
    var today by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
            val delayMs = between(now, nextMidnight).toMillis()
            delay(delayMs.milliseconds)
            today = LocalDate.now()
        }
    }

    return today
}