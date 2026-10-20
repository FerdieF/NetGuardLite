package com.example.netguardlite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.netguardlite.data.ScanEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val timeFormat = SimpleDateFormat("dd MM yyyy, HH:mm:ss", Locale.forLanguageTag("id-ID"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(items: List<ScanEntity>, onClear: () -> Unit) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pemeriksaan", fontWeight = FontWeight.Bold) },
                actions = {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = "Hapus Riwayat")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Tidak ada riwayat pemeriksaan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                    HistoryCard(item, previous = items.getOrNull(index + 1))
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: ScanEntity, previous: ScanEntity?) {
    val ok = item.outcome == "SUCCESS"
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (ok) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = null,
                    tint = if (ok) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                    )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = when (item.outcome) {
                        "SUCCESS" -> "Berhasil (HTTP ${item.httpCode}) dalam ${item.durationMs} ms"
                        "TIMEOUT" -> "Gagal: ${item.detail}"
                        else -> "Gagal: ${item.detail ?: "Tidak diketahui"}"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                timeFormat.format(Date(item.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "${item.connectionType} • IP ${item.localIp ?: "-"} • VPN ${if (item.vpnActive) "aktif" else "tidak aktif"}",
                style = MaterialTheme.typography.bodySmall
            )
            compareText(item, previous)?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun compareText(now: ScanEntity, before: ScanEntity?): String? {
    if (before == null) return null
    if (now.outcome == "SUCCESS" && before.outcome == "SUCCESS"){
        val delta = now.durationMs!! - before.durationMs!!
        val sign = if (delta >= 0) "+" else ""
        return "$sign$delta ms dibanding pemeriksaan sebelumnya"
    }
    if (now.outcome != before.outcome) return "Hasil berbeda dari pemeriksaan sebelumnya"
    return null
}