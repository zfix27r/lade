package app.lade.more.ui.internal.backup

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lade.resources.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val BACKUP_FILE_NAME_FORMAT = "lade-backup-%s.zip"
private const val BACKUP_TIMESTAMP_PATTERN = "yyyy-MM-dd-HHmm"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

    val restartTitle = stringResource(R.string.backup_restart_title)
    val restartMessage = stringResource(R.string.backup_restart_message)
    val restartConfirm = stringResource(R.string.backup_restart_confirm)

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/zip"),
    ) { uri ->
        uri?.let { viewModel.export(it.toString()) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { viewModel.import(it.toString()) }
    }

    val messageText: String? = state.message?.let { stringResource(it) }

    LaunchedEffect(messageText) {
        messageText?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    if (state.showRestartDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(restartTitle) },
            text = { Text(restartMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.consumeRestartDialog()
                        restartApp(context)
                    },
                ) {
                    Text(restartConfirm)
                }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.backup_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(dimensionResource(R.dimen.screen_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_md)),
        ) {
            Button(
                onClick = {
                    val timestamp = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern(BACKUP_TIMESTAMP_PATTERN),
                    )
                    exportLauncher.launch(BACKUP_FILE_NAME_FORMAT.format(timestamp))
                },
                enabled = !state.isRunning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.backup_export))
            }
            Button(
                onClick = { importLauncher.launch(arrayOf("application/zip")) },
                enabled = !state.isRunning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.backup_import))
            }
            if (state.isRunning) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(state.progress.label())
            }
        }
    }
}

@Composable
private fun BackupUiProgress.label(): String = when (this) {
    BackupUiProgress.Idle -> ""
    BackupUiProgress.Preparing -> stringResource(R.string.backup_progress_preparing)
    BackupUiProgress.Copying -> stringResource(R.string.backup_progress_copying)
    BackupUiProgress.Finalizing -> stringResource(R.string.backup_progress_finalizing)
}

private fun restartApp(context: Context) {
    val intent = context.packageManager
        .getLaunchIntentForPackage(context.packageName)
        ?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    if (intent != null) {
        context.startActivity(intent)
    }
    Process.killProcess(Process.myPid())
}