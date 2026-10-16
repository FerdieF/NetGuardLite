package com.example.netguardlite.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import com.example.netguardlite.model.ConnectionType
import com.example.netguardlite.model.NetworkStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.net.Inet4Address

class NetworkRepository(context: Context) {
    private val cm =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun snapshot(): NetworkStatus {
        val active = cm.activeNetwork ?: return NetworkStatus()
        val caps = cm.getNetworkCapabilities(active) ?: return NetworkStatus()

        val vpnActive = caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

        val type = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
            vpnActive -> ConnectionType.VPN
            else -> ConnectionType.OTHER
        }

        // Kalau VPN aktif, LinkProperties jaringan aktif adalah milik tunnel VPN.
        // Cari jaringan fisik di bawahnya supaya IP/Gateway yang tampil milik Wi-Fi/seluler.
        val base = if (vpnActive) findUnderlyingNetwork() ?: active else active
        val link = cm.getLinkProperties(base)

        val localIp = link?.linkAddresses
            ?.firstOrNull { it.address is Inet4Address }
            ?.address?.hostAddress

        val gateway = link?.routes
            ?.firstOrNull { it.isDefaultRoute && it.gateway is Inet4Address }
            ?.gateway?.hostAddress

        val dns = link?.dnsServers?.mapNotNull { it.hostAddress } ?: emptyList()

        return NetworkStatus(
            type = type,
            hasInternetCapability = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET),
            validated = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED),
            vpnActive = vpnActive,
            localIp = localIp,
            gateway = gateway,
            dnsServers = dns
        )
    }

    fun observe(): Flow<NetworkStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(snapshot()) }
            override fun onLost(network: Network) { trySend(NetworkStatus()) }
            override fun onCapabilitiesChanged(
                network: Network,
                caps: NetworkCapabilities
            ) { trySend(snapshot()) }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                trySend(snapshot())
            }
        }
        cm.registerDefaultNetworkCallback(callback)
        trySend(snapshot())
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    @Suppress("DEPRECATION")
    private fun findUnderlyingNetwork(): Network? =
        cm.allNetworks.firstOrNull { net ->
            val c = cm.getNetworkCapabilities(net)
            c != null &&
                    !c.hasTransport(NetworkCapabilities.TRANSPORT_VPN) &&
                    c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
}