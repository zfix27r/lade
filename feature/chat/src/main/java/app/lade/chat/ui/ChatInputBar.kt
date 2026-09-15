package app.lade.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lade.chat.domain.ChatChoiceOption
import app.lade.resources.R
import kotlin.collections.forEach

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatInputBar(
    bottomNavHeight: Dp,
    input: String,
    sending: Boolean,
    choices: List<ChatChoiceOption>,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onChoice: (ChatChoiceOption) -> Unit,
) {
    val bottomInsets = if (bottomNavHeight > 0.dp) {
        WindowInsets.ime.union(WindowInsets(bottom = bottomNavHeight))
    } else {
        WindowInsets.ime
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(bottomInsets.only(WindowInsetsSides.Bottom)),
    ) {
        if (choices.isNotEmpty()) {
            Text(
                text = stringResource(R.string.chat_choice_prompt),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.screen_padding)),
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
            ) {
                choices.forEach { option ->
                    FilterChip(
                        selected = false,
                        onClick = { onChoice(option) },
                        label = { Text(option.label) },
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.screen_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm)),
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.chat_input_hint)) },
                singleLine = true,
                enabled = !sending,
            )
            IconButton(
                onClick = onSend,
                enabled = !sending && input.isNotBlank(),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.chat_send),
                )
            }
        }
    }
}