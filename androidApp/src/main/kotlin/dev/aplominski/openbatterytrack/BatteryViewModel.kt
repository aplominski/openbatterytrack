package dev.aplominski.openbatterytrack

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BatteryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = BatteryRepository(app.applicationContext)

    private val _state = MutableStateFlow(repo.readCurrent())
    val state: StateFlow<BatteryUiState> = _state.asStateFlow()

    private val _hasUpdate = MutableStateFlow(false)
    val hasUpdate: StateFlow<Boolean> = _hasUpdate.asStateFlow()

    private val _updateUrl = MutableStateFlow<String?>(null)
    val updateUrl: StateFlow<String?> = _updateUrl.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            _state.value = repo.map(intent)
        }
    }

    init {
        getApplication<Application>().registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        viewModelScope.launch {
            val rel = GithubUpdates.fetchLatest() ?: return@launch
            val installed = installedVersionCode()
            val newest = GithubUpdates.buildNumber(rel.tag)
            _updateUrl.value = rel.url
            _hasUpdate.value = newest == null || newest > installed
        }
    }

    @Suppress("DEPRECATION")
    private fun installedVersionCode(): Int {
        val app = getApplication<Application>()
        val info = app.packageManager.getPackageInfo(app.packageName, 0)
        return if (android.os.Build.VERSION.SDK_INT >= 28) info.longVersionCode.toInt()
        else info.versionCode
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(receiver)
        super.onCleared()
    }
}
