package app.lade.entrydetailsscreen.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.lade.entrydetailsscreen.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSettingsRow(
    date: LocalDate?,
    onDateChange: (LocalDate?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val today = LocalDate.now()

    SettingsRow(
        icon = Icons.Default.CalendarToday,
        label = stringResource(R.string.entry_settings_date),
        value = date?.format(
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault()),
        ) ?: stringResource(R.string.entry_settings_not_set),
        onClick = { expanded = !expanded },
    )

    if (expanded) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = (date ?: today)
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        )
        DatePicker(
            state = pickerState,
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        )
        // Слушаем изменение
        LaunchedEffect(pickerState.selectedDateMillis) {
            pickerState.selectedDateMillis?.let { millis ->
                val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                if (picked != date) onDateChange(picked)
            }
        }
    }
}