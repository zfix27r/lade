package app.lade.calendar.internal.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import app.lade.calendar.internal.appbar.data.icon
import app.lade.calendar.internal.appbar.data.labelRes
import app.lade.calendar.internal.domain.mode.CalendarMode
import app.lade.resources.R

@Composable
internal fun AppBarModeBtn(
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