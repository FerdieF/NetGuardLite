package com.example.netguardlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.netguardlite.ui.DashboardScreen
import com.example.netguardlite.ui.HistoryScreen
import com.example.netguardlite.ui.NetGuardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NetGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val status by viewModel.status.collectAsStateWithLifecycle()
            val test by viewModel.test.collectAsStateWithLifecycle()
            val checked by viewModel.checked.collectAsStateWithLifecycle()
            val history by viewModel.history.collectAsStateWithLifecycle()
            var tab by rememberSaveable { mutableIntStateOf(0) }

            MaterialTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = tab == 0,
                                onClick = { tab = 0 },
                                icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                                label = { Text("Dashboard") }
                            )
                            NavigationBarItem(
                                selected = tab == 1,
                                onClick = { tab = 1 },
                                icon = { Icon(Icons.Filled.History, contentDescription = null) },
                                label = { Text("Riwayat") }
                            )
                        }
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        when (tab) {
                            0 -> DashboardScreen(
                                status = status,
                                test = test,
                                checked = checked,
                                onRunTest = viewModel::runTest,
                                onToggleChecklist = viewModel::toggleChecklist
                            )
                            else -> HistoryScreen(
                                items = history,
                                onClear = viewModel::clearHistory
                            )
                        }
                    }
                }
            }
        }
    }
}