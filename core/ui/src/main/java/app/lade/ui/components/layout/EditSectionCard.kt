package app.lade.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import app.lade.resources.R

@Composable
fun EditSectionCard(
	title: String,
	modifier: Modifier = Modifier,
	content: @Composable ColumnScope.() -> Unit,
) {
	OutlinedCard(modifier = modifier.fillMaxWidth()) {
		Column(
			modifier = Modifier.padding(dimensionResource(R.dimen.spacing_lg)),
			verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
			content = {
				Text(
					text = title,
					style = MaterialTheme.typography.titleSmall,
					color = MaterialTheme.colorScheme.onSurface,
				)
				content()
			},
		)
	}
}
