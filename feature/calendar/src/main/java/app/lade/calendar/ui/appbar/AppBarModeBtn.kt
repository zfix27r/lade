package app.lade.calendar.ui.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.lade.calendar.domain.CalendarMode
import app.lade.resources.R

@Composable
fun AppBarModeBtn(
    mode: CalendarMode,
    onModeChange: (CalendarMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = mode.icon(),
                contentDescription = stringResource(R.string.calendar_mode_menu_cd),
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CalendarMode.entries.forEach { item ->
                DropdownMenuItem(
                    text = { Text(stringResource(item.labelRes())) },
                    onClick = {
                        expanded = false
                        onModeChange(item)
                    },
                    leadingIcon = { Icon(item.icon(), contentDescription = null) },
                    trailingIcon = if (item == mode) {
                        {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

private fun CalendarMode.icon() = when (this) {
    CalendarMode.LIST -> Icons.AutoMirrored.Filled.ViewList
    CalendarMode.DAY -> Icons.Filled.CalendarViewDay
    CalendarMode.DAY_3 -> Icons.Filled.ViewWeek
    CalendarMode.WEEK -> Icons.Filled.CalendarViewWeek
    CalendarMode.MONTH -> Icons.Filled.CalendarMonth
    CalendarMode.YEAR -> Icons.Filled.CalendarToday
}

private fun CalendarMode.labelRes() = when (this) {
    CalendarMode.LIST -> R.string.calendar_view_feed
    CalendarMode.DAY -> R.string.calendar_view_day
    CalendarMode.DAY_3 -> R.string.calendar_view_day_3
    CalendarMode.WEEK -> R.string.calendar_view_week
    CalendarMode.MONTH -> R.string.calendar_view_month
    CalendarMode.YEAR -> R.string.calendar_view_year
}