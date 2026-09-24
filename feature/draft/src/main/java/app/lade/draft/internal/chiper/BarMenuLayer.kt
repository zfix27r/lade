package app.lade.draft.internal.chiper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lade.draft.internal.bar.BarMenu
import app.lade.draftdata.DraftModel
import app.lade.ui.theme.LadeMotion
import app.lade.ui.theme.Spacing

@Composable
internal fun BarMenuLayer(
    activeMenu: BarMenu?,
    draft: DraftModel,
    onRecurrenceChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = activeMenu != null,
        enter = expandVertically(animationSpec = LadeMotion.enter()),
        exit = shrinkVertically(animationSpec = LadeMotion.exit()),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        ) {
            Crossfade(targetState = activeMenu, animationSpec = LadeMotion.contentSize()) { menu ->
                when (menu) {
                    BarMenu.Date -> BarMenuDate(draft = draft)
                    BarMenu.Time -> BarMenuTime(draft = draft)
                    BarMenu.Recurrence -> BarMenuRecurrence(
                        draft = draft,
                        onRecurrenceChange = onRecurrenceChange,
                    )
                    BarMenu.Notifications -> BarMenuNotifications(draft = draft)
                    BarMenu.Goals -> BarMenuGoals(draft = draft)
                    null -> Unit
                }
            }
        }
    }
}