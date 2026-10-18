package com.example.netguardlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.netguardlite.ui.DashboardScreen
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

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen(
                        status = status,
                        test = test,
                        checked = checked,
                        onRunTest = viewModel::runTest,
                        onToggleChecklist = viewModel::toggleChecklist
                    )

                }
            }
        }
    }
}