package app.lade.calendar.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CreateKindRow(
    title: String,
    hint: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        leadingContent = icon,
        trailingContent = null,
        overlineContent = null,
        supportingContent = { Text(hint) },
        colors = ListItemDefaults.colors(),
        elevation = ListItemDefaults.elevation(ListItemDefaults.Elevation),
        content = { Text(title) },
    )
}