package com.example.netguardlite.network

import android.os.SystemClock
import com.example.netguardlite.model.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import javax.net.ssl.SSLException


class ConnectivityTester {
    companion object {
        //Menggunakan endpoint uji publik stabil (misalkan: Google generate_204)
        private const val TEST_URL = "https://www.google.com/generate_204"
        private const val TIMEOUT_MS = 5_000
    }

    suspend fun run(): TestResult = withContext(Dispatchers.IO) {
        val start = SystemClock.elapsedRealtime()
        fun elapsed() = SystemClock.elapsedRealtime() - start

        var connection: HttpURLConnection? = null
        try{
            connection= (URL(TEST_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                requestMethod = "GET"
            }
            val code = connection.responseCode
            if (code in 200..209) TestResult.Success(code, elapsed())
            else TestResult.Failure("Server membalas HTTP $code", elapsed())
        } catch (e: SocketTimeoutException) {
            TestResult.Timeout(TIMEOUT_MS.toLong())
        } catch (e: UnknownHostException) {
            TestResult.Failure("Nama host tidak ditemukan (DNS gagal)", elapsed())
        } catch (e: ConnectException){
            TestResult.Failure("Tidak bisa terhubung ke server", elapsed())
        } catch (e: SSLException) {
            TestResult.Failure("Handshake HTTPS/TLS gagal", elapsed())
        } catch (e: java.io.IOException) {
            TestResult.Failure("Kesalahan jaringan: ${e.javaClass.simpleName}", elapsed())
        } finally {
            connection?.disconnect()
        }
    }
}