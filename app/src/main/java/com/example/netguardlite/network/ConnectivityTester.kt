package com.example.netguardlite.network

import com.example.netguardlite.model.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.measureTimeMillis
import android.os.SystemClock
import java.io.IOException
import java.net.SocketTimeoutException


class ConnectivityTester {
    companion object {
        //Menggunakan endpoint uji publik stabil (misalkan: Google generate_204)
        private const val TEST_URL = "https://www.google.com/generate_204"
    }

    suspend fun run(): TestResult = withContext(Dispatchers.IO) {
        val start = SystemClock.elapsedRealtime()
        try{
            val conn = (URL(TEST_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 5_000; readTimeout = 5_000; requestMethod = "GET"
            }
            val code = conn.responseCode
            conn.disconnect()
            val ms = SystemClock.elapsedRealtime() - start
            if (code in 200..209) TestResult.Success(code, ms)
            else TestResult.Failure("HTTP $code", ms)
        } catch (e: SocketTimeoutException) {
            TestResult.Timeout(5_000)
        } catch (e: IOException) {
            TestResult.Failure(e.javaClass.simpleName, SystemClock.elapsedRealtime() - start)
        }
    }
}