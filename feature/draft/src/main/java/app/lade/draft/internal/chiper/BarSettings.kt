package app.lade.draft.internal.chiper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.lade.draft.internal.bar.BarMenu
import app.lade.draft.internal.bar.icon
import app.lade.draft.internal.bar.status
import app.lade.draftdata.DraftModel
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing

@Composable
internal fun BarSettings(
    draft: DraftModel,
    isFocused: Boolean,
    activeMenu: BarMenu?,
    onMenuClick: (BarMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visible = !draft.isEmpty || isFocused

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = LadeMotion.enter()) + expandVertically(animationSpec = LadeMotion.enter()),
        exit = fadeOut(animationSpec = LadeMotion.exit()) + shrinkVertically(animationSpec = LadeMotion.exit()),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BarMenu.entries.forEach { menu ->
                BarSettingsButton(
                    icon = menu.icon(),
                    label = menu.status(draft),
                    selected = activeMenu == menu,
                    onClick = { onMenuClick(menu) },
                )
            }
        }
    }
}