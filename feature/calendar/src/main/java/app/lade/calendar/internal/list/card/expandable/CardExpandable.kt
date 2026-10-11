package app.lade.calendar.internal.list.card.expandable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import app.lade.calendar.internal.list.ListCard

@Composable
internal fun CardExpandable(
    cornerRadius: Dp,
    stripeColor: Color,
    stripeWidth: Dp,
    containerColor: Color,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    summary: @Composable () -> Unit,
    details: @Composable () -> Unit,
) {
    ListCard(
        cornerRadius = cornerRadius,
        stripeColor = stripeColor,
        stripeWidth = stripeWidth,
        containerColor = containerColor,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            summary()

            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    (fadeIn() + expandVertically()) togetherWith
                        (fadeOut() + shrinkVertically())
                },
                label = "CardExpandableDetails",
            ) { isExpanded ->
                if (isExpanded) {
                    details()
                }
            }
        }
    }
}