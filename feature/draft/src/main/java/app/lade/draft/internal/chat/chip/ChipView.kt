package app.lade.draft.internal.chat.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.draft.internal.chat.chip.domain.ChipModel
import app.lade.draft.internal.di.ChipEntryPoint
import app.lade.ui.theme.Spacing
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ChipView(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val port = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ChipEntryPoint::class.java,
        ).chipControlPort()
    }
    val controlPort = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ChipEntryPoint::class.java,
        ).chipControlPort()
    }

    val data by port.chips.collectAsStateWithLifecycle()
    val states by port.states.collectAsStateWithLifecycle()

    if (data.isEmpty()) return

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        data.forEach { chip ->
            key(chip.key) {
                ChipRow(
                    chip = ChipModel(
                        data = chip,
                        state = states.stateOf(chip.key),
                        emphasis = states.emphasisOf(chip.key),
                    ),
                    onClick = { controlPort.onClickEdit(chip.key) },
                    onRemove = { controlPort.onClickRemove(chip.key) },
                )
            }
        }
    }
}