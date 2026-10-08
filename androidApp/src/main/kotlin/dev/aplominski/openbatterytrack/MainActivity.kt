package dev.aplominski.openbatterytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            OpenBatteryTrackerTheme {
                val vm: BatteryViewModel = viewModel()
                val state by vm.state.collectAsState()
                BatteryScreen(state = state)
            }
        }
    }
}
