package app.lade.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import app.lade.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
	Scaffold(
		topBar = {
			TopAppBar(title = { Text(stringResource(R.string.nav_chat)) })
		},
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(dimensionResource(R.dimen.screen_padding)),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
		) {
			Text(
				text = stringResource(R.string.chat_stub),
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}
