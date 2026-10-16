package com.example.netguardlite.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis

class ConnectivityTester {
    companion object {
        //Menggunakan endpoint uji publik stabil (misalkan: Google generate_204)
        private const val TEST_URL = "https://www.google.com/generate_204"
    }

    suspend fun runTest(): String = withContext(Dispatchers.IO) {
        var resultMessage: String
        val duration = measureTimeMillis {
            try {
                val url = URL(TEST_URL)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 5000 //Timeout 5 detik
                    readTimeout = 5000
                    requestMethod = "GET"
                }

                val responseCode = connection.responseCode
                resultMessage = if (responseCode == 204 || responseCode in 200..299) {
                    "Berhasil (HTTP $responseCode)"
                } else {
                    "Gagal (HTTP $responseCode)"
                }
                connection.disconnect()
            } catch (e: java.net.SocketTimeoutException) {
                resultMessage = "Gagal: Timeout(Waktu habis)"
            } catch (e: java.io.IOException) {
                resultMessage = "Gagal: Tidak ada internet"
            } catch (e: Exception) {
                resultMessage = "Gagal: ${e.localizedMessage ?: "Kesalahan tidak dikenal"}"
            }
        }

        "$resultMessage dalam $duration ms"
    }
}