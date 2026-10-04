package app.lade.more.ui.internal.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lade.backup.api.BackupFormat
import app.lade.backup.api.BackupPort
import app.lade.backup.api.BackupProgress
import app.lade.backup.api.BackupResult
import app.lade.resources.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupPort: BackupPort,
) : ViewModel() {

    private val _state = MutableStateFlow(BackupUiState())
    val state: StateFlow<BackupUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            backupPort.progress.collect { progress ->
                _state.update { it.copy(progress = progress.toUi()) }
            }
        }
    }

    fun export(uri: String) = run(uri, isExport = true)

    fun import(uri: String) = run(uri, isExport = false)

    fun consumeMessage() {
        _state.update { it.copy(message = null) }
    }

    fun consumeRestartDialog() {
        _state.update { it.copy(showRestartDialog = false) }
    }

    private fun run(uri: String, isExport: Boolean) {
        if (_state.value.isRunning) return
        viewModelScope.launch {
            _state.update { it.copy(isRunning = true, message = null) }
            val result = if (isExport) {
                backupPort.export(uri, BackupFormat.DB)
            } else {
                backupPort.import(uri, BackupFormat.DB)
            }
            when (result) {
                BackupResult.Success -> _state.update {
                    it.copy(
                        isRunning = false,
                        progress = BackupUiProgress.Idle,
                        message = R.string.backup_export_success,
                    )
                }
                BackupResult.RestartRequired -> _state.update {
                    it.copy(
                        isRunning = false,
                        progress = BackupUiProgress.Idle,
                        showRestartDialog = true,
                    )
                }
                is BackupResult.Failure -> _state.update {
                    it.copy(
                        isRunning = false,
                        progress = BackupUiProgress.Idle,
                        message = null,
                    )
                }
            }
        }
    }
}

private fun BackupProgress.toUi(): BackupUiProgress = when (this) {
    BackupProgress.Preparing -> BackupUiProgress.Preparing
    BackupProgress.Copying -> BackupUiProgress.Copying
    BackupProgress.Finalizing -> BackupUiProgress.Finalizing
}