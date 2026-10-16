package com.example.netguardlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.netguardlite.network.ConnectivityTester
import com.example.netguardlite.network.NetworkRepository
import com.example.netguardlite.ui.DashboardScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var networkRepository: NetworkRepository
    private val connectivityTester = ConnectivityTester()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkRepository = NetworkRepository(applicationContext)

        val initialStatus = networkRepository.getNetworkStatus()

        enableEdgeToEdge()
        setContent {
            var dashboardState by remember { mutableStateOf(initialStatus) }
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen(
                        state = dashboardState,
                        onRunTest = {
//                            Nanti diisi
                            dashboardState = dashboardState.copy(
                                testRunning = true,
                                testResult = "Menguji koneksi ke server..."
                            )
//                            Jalankan tes di background menggunakan coroutine
                            lifecycleScope.launch{
                                val result = connectivityTester.runTest()
//                                Update state dengan hasil tes terbaru
                                dashboardState = dashboardState.copy(
                                    testRunning = false,
                                    testResult = result
                                )
                            }
                        },
                        onSecurityChanged = { itemKey, isChecked ->
//                            update map checklist keamanan saat checkbox diklik
                            val updatedChecklist = dashboardState.securityChecklist.toMutableMap().apply {
                                this[itemKey] = isChecked
                            }
                            dashboardState = dashboardState.copy(securityChecklist = updatedChecklist)
                        }
                    )
                }
            }
        }
    }
}