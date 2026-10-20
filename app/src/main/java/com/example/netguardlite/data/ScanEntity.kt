package com.example.netguardlite.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.netguardlite.model.NetworkStatus
import com.example.netguardlite.model.TestResult

@Entity(tableName = "scan_history")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val connectionType: String,
    val localIp: String?,
    val gateway: String?,
    val vpnActive: Boolean,
    val validated: Boolean,
    val outcome: String,
    val httpCode: Int?,
    val durationMs: Long?,
    val detail: String?
)

//  Ubah hasil tes + kondisi jaringan saat itu jadi satu baris riwayat. null = belum selesai
fun buildScanEntity(status: NetworkStatus, result: TestResult): ScanEntity? {
    val base = ScanEntity(
        timestamp = System.currentTimeMillis(),
        connectionType = status.type.label,
        localIp = status.localIp,
        gateway = status.gateway,
        vpnActive = status.vpnActive,
        validated = status.validated,
        outcome = "",
        httpCode = null,
        durationMs = null,
        detail = null
    )

    return when (result) {
        is TestResult.Success ->
            base.copy(outcome = "SUCCESS", httpCode = result.httpCode, durationMs = result.durationMs)
        is TestResult.Failure ->
            base.copy(outcome = "FAILURE", detail = result.reason)
        is TestResult.Timeout ->
            base.copy(outcome = "TIMEOUT", detail = "Waktu habis (${result.limitMs} ms)")
        else -> null
    }
}