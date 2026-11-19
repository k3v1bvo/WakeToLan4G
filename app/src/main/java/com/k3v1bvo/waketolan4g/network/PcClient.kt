package com.k3v1bvo.waketolan4g.network

import com.google.gson.Gson
import com.k3v1bvo.waketolan4g.data.PcStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class PcClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    suspend fun getStatus(host: String, port: Int, token: String): Result<PcStatus> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val url = "http://$host:$port/api/status"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - start
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP Error: ${response.code}"))
                }
                val body = response.body?.string() ?: ""
                val status = gson.fromJson(body, PcStatus::class.java).copy(
                    online = true,
                    latencyMs = elapsed
                )
                Result.success(status)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeCommand(host: String, port: Int, token: String, endpoint: String, payloadJson: String = "{}"): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = "http://$host:$port/api/$endpoint"
            val body = payloadJson.toRequestBody(jsonMedia)
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(response.body?.string() ?: "OK")
                } else {
                    Result.failure(Exception("Error ${response.code}: ${response.message}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
