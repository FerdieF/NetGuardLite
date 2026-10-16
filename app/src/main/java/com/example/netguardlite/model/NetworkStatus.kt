package com.example.netguardlite.model

enum class ConnectionType(val label: String) {
    WIFI("Wi-Fi"),
    CELLULAR("Data seluler"),
    ETHERNET("Ethernet"),
    VPN("VPN"),
    OTHER("Lainnya"),
    NONE("Tidak terhubung")
}

/**
 * Snapshot kondisi jaringan yang TERDETEKSI oleh Android.
 * Nilai null = informasi tidak tersedia (dibatasi versi Android / hak akses),
 * bukan berarti aman atau tidak aman.
 */
data class NetworkStatus(
    val type: ConnectionType = ConnectionType.NONE,
    val hasInternetCapability: Boolean = false,
    val validated: Boolean = false,      // Android berhasil memverifikasi akses internet
    val vpnActive: Boolean = false,
    val localIp: String? = null,
    val gateway: String? = null,
    val dnsServers: List<String> = emptyList()
) {
    val connected: Boolean get() = type != ConnectionType.NONE
}

sealed interface TestResult {
    data object Idle : TestResult
    data object Running : TestResult
    data class Success(val httpCode: Int, val durationMs: Long) : TestResult
    data class Failure(val reason: String, val durationMs: Long) : TestResult
    data class Timeout(val limitMs: Long) : TestResult
}
