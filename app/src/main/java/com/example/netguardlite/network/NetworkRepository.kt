package com.example.netguardlite.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.netguardlite.ui.DashboardState
import java.net.InetAddress
import java.net.NetworkInterface

class NetworkRepository(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun getNetworkStatus(): DashboardState {
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        val isConnected = capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        val isVpnActive = capabilities != null &&
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

        val connectionType = when {
            capabilities == null -> "Tidak terhubung"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Data Seluler"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            isVpnActive -> "VPN"
            else -> "Lainnya"
        }

        val localIp = if (isConnected) getLocalIpAddress() else null

        return DashboardState(
            connected = isConnected,
            connectionType = connectionType,
            localIp = localIp,
            gateway = "Mendukung versi berikutnya",
            vpnActive = isVpnActive
        )
    }

    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is InetAddress) {
                        val hostAddress = address.hostAddress
                        //Ambil IPv4 standar
                        if (hostAddress != null && hostAddress.indexOf(':') < 0) {
                            return hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}