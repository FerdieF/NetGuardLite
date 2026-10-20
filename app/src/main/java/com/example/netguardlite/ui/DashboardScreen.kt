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
import com.example.netguardlite.model.ConnectionType
import com.example.netguardlite.model.NetworkStatus
import com.example.netguardlite.model.TestResult

/** Item checklist manual: id disimpan di ViewModel, label ditampilkan di UI. */
private val manualChecklist = listOf(
    "wpa" to "Wi-Fi memakai WPA2/WPA3",
    "admin" to "Password admin router sudah diganti dari bawaan",
    "firmware" to "Firmware router sudah diperbarui",
    "wps" to "WPS dimatikan"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    status: NetworkStatus,
    test: TestResult,
    checked: Set<String>,
    onRunTest: () -> Unit,
    onToggleChecklist: (String) -> Unit
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
            item { StatusCard(status) }
            item {
                InfoCard(
                    rows = listOf(
                        Triple(Icons.Filled.Wifi, "Jenis koneksi", status.type.label),
                        Triple(
                            Icons.Filled.Dns, "DNS",
                            status.dnsServers
                                .sortedBy { it.contains(':') }   // IPv4 dulu, IPv6 setelahnya
                                .joinToString("\n")
                                .ifEmpty { "Tidak tersedia" }
                        ),
                        Triple(Icons.Filled.Router, "Gateway", status.gateway ?: "Tidak tersedia"),
                        Triple(
                            Icons.Filled.Dns, "DNS",
                            status.dnsServers.joinToString().ifEmpty { "Tidak tersedia" }
                        ),
                        Triple(Icons.Filled.VpnKey, "VPN", if (status.vpnActive) "Aktif" else "Tidak aktif")
                    )
                )
            }
            item { TestCard(test, onRunTest) }
            item { ChecklistCard(status, checked, onToggleChecklist) }
            item {
                Text(
                    "Hanya menampilkan kondisi yang terdeteksi saat ini. " +
                            "Bukan audit keamanan penuh.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------- Komponen ----------

@Composable
private fun StatusCard(status: NetworkStatus) {
    val (icon, text, good) = when {
        !status.connected -> Triple(Icons.Filled.WifiOff, "Tidak terhubung", false)
        status.validated -> Triple(Icons.Filled.CheckCircle, "Terhubung, internet terverifikasi", true)
        else -> Triple(Icons.Filled.Warning, "Terhubung, internet belum terverifikasi", false)
    }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (good) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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

private fun TestResult.describe(): String = when (this) {
    TestResult.Idle -> "Belum dijalankan"
    TestResult.Running -> "Menguji koneksi…"
    is TestResult.Success -> "Berhasil (HTTP $httpCode) dalam $durationMs ms"
    is TestResult.Failure -> "Gagal: $reason ($durationMs ms)"
    is TestResult.Timeout -> "Gagal: waktu habis (lebih dari $limitMs ms)"
}

@Composable
private fun TestCard(result: TestResult, onRun: () -> Unit) {
    val running = result is TestResult.Running
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Connectivity Test", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(result.describe(), style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onRun, enabled = !running) {
                Text(if (running) "Menguji…" else "Run Test")
            }
        }
    }
}

@Composable
private fun ChecklistCard(
    status: NetworkStatus,
    checked: Set<String>,
    onToggle: (String) -> Unit
) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Security Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            // --- Terdeteksi otomatis (read-only) ---
            Text(
                "Terdeteksi otomatis",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            AutoRow("VPN aktif", if (status.vpnActive) "Terdeteksi aktif" else "Tidak terdeteksi", status.vpnActive)
            AutoRow(
                "Internet terverifikasi Android",
                if (status.validated) "Ya" else "Tidak / belum",
                status.validated
            )
            AutoRow("Jenis enkripsi Wi-Fi", "Tidak dapat dideteksi aplikasi ini, periksa manual", null)

            HorizontalDivider()

            // --- Manual ---
            Text(
                "Dicek manual oleh kamu",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            manualChecklist.forEach { (id, label) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Checkbox(checked = id in checked, onCheckedChange = { onToggle(id) })
                }
            }
        }
    }
}

/** ok: true = terdeteksi, false = tidak terdeteksi, null = tidak bisa ditentukan. */
@Composable
private fun AutoRow(title: String, detail: String, ok: Boolean?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = when (ok) {
                true -> Icons.Filled.CheckCircle
                false -> Icons.Filled.RadioButtonUnchecked
                null -> Icons.Filled.Info
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                detail, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------- Preview ----------

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    MaterialTheme {
        DashboardScreen(
            status = NetworkStatus(
                type = ConnectionType.WIFI,
                hasInternetCapability = true,
                validated = true,
                localIp = "192.168.1.23",
                gateway = "192.168.1.1",
                dnsServers = listOf("8.8.8.8")
            ),
            test = TestResult.Success(204, 143),
            checked = setOf("admin"),
            onRunTest = {},
            onToggleChecklist = {}
        )
    }
}