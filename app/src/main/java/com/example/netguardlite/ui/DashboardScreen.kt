package com.example.netguardlite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ---------- State layar (nanti diisi dari NetworkRepository / ViewModel) ----------

data class DashboardState(
    val connected: Boolean = false,
    val connectionType: String = "Tidak terhubung",
    val localIp: String? = null,
    val gateway: String? = null,
    val vpnActive: Boolean = false,
    val testResult: String = "Belum dijalankan",
    val testRunning: Boolean = false,

//    Tambahan state untuk checklist keamanan
    val securityChecklist: Map<String, Boolean> = mapOf(
        "Menggunakan enkripsi WPA2/WPA3" to false,
        "Sudah mengganti password default router" to false,
        "Firmware router sudah diperbarui" to false,
    )
)

// ---------- Layar utama ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState = DashboardState(),
    onRunTest: () -> Unit = {},
    onSecurityChanged: (String, Boolean) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("NetGuard Lite", fontWeight = FontWeight.Bold)
                        Text(
                            "Kondisi jaringan yang terdeteksi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StatusCard(state.connected) }
            item {
                InfoCard(
                    rows = listOf(
                        Triple(Icons.Filled.Wifi, "Jenis koneksi", state.connectionType),
                        Triple(Icons.Filled.Dns, "IP lokal", state.localIp ?: "Tidak tersedia"),
                        Triple(Icons.Filled.Router, "Gateway", state.gateway ?: "Tidak tersedia"),
                        Triple(Icons.Filled.VpnKey, "VPN", if (state.vpnActive) "Aktif" else "Tidak aktif")
                    )
                )
            }
            item { TestCard(state.testResult, state.testRunning, onRunTest) }
            item {
                Text(
                    "Hanya menampilkan kondisi yang terdeteksi. " +
                            "Bukan audit keamanan penuh.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                SecurityCard(
                    checklist = state.securityChecklist,
                    onCheckedChange = onSecurityChanged
                )
            }
        }
    }
}

// ---------- Komponen ----------

@Composable
private fun StatusCard(connected: Boolean) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (connected) Icons.Filled.CheckCircle else Icons.Filled.WifiOff,
                contentDescription = null,
                tint = if (connected) MaterialTheme.colorScheme.tertiary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                if (connected) "Terhubung" else "Tidak terhubung",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SecurityCard(
    checklist: Map<String, Boolean>,
    onCheckedChange: (String, Boolean) -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Security Checklist",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Pemeriksaan manual untuk kebiasaan dan keamanan router.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            checklist.forEach { (item, isChecked) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            onCheckedChange(item, checked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(rows: List<Triple<ImageVector, String, String>>) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Informasi Jaringan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            rows.forEach { (icon, label, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            label, style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun TestCard(result: String, running: Boolean, onRun: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Connectivity Test", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(result, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onRun, enabled = !running) {
                Text(if (running) "Menguji…" else "Run Test")
            }
        }
    }
}

// ---------- Preview (lihat tampilan di Android Studio tanpa run ke HP) ----------

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    MaterialTheme {
        DashboardScreen(
            DashboardState(
                connected = true,
                connectionType = "Wi-Fi",
                localIp = "192.168.1.23",
                gateway = "192.168.1.1",
                testResult = "Berhasil (HTTP 204) dalam 143 ms"
            )
        )
    }
}
