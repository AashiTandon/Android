package com.example.datamonitor.network

import android.util.Log
import com.example.datamonitor.data.LocationData
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

object DataUploader {

    // ⚠️ IMPORTANT: Replace with your actual server URL
    // For PHP version: Use your domain (e.g., "http://yourdomain.com" or "http://yourdomain.com/data-monitor")
    // For Node.js version: Use your domain with port (e.g., "http://yourdomain.com:3000")
    private const val SERVER_URL = "http://ruhanixlegal.in/tracking-app/server-php"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Upload location data to server with retry logic
     */
    fun uploadLocation(locationData: LocationData) {
        var lastException: Exception? = null

        // Try up to 3 times
        repeat(3) { attempt ->
            try {
                val json = gson.toJson(locationData)
                val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url("$SERVER_URL/api/location.php")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "No error details"
                        throw IOException("Upload failed: ${response.code} - $errorBody")
                    }
                    Log.d("DataUploader", "✅ Location uploaded successfully (attempt ${attempt + 1})")
                    return  // Success, exit function
                }
            } catch (e: Exception) {
                lastException = e
                Log.w("DataUploader", "⚠️ Location upload attempt ${attempt + 1} failed: ${e.message}")

                // Wait before retry (exponential backoff)
                if (attempt < 2) {
                    Thread.sleep((attempt + 1) * 1000L)  // 1s, 2s
                }
            }
        }

        // All attempts failed
        Log.e("DataUploader", "❌ Location upload failed after 3 attempts", lastException)
        throw lastException ?: IOException("Upload failed")
    }

    /**
     * Upload audio file to server with retry logic
     */
    fun uploadAudio(audioFile: File) {
        if (!audioFile.exists()) {
            throw IOException("Audio file does not exist: ${audioFile.name}")
        }

        var lastException: Exception? = null

        // Try up to 3 times
        repeat(3) { attempt ->
            try {
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "audio",
                        audioFile.name,
                        audioFile.asRequestBody("audio/pcm".toMediaType())
                    )
                    .addFormDataPart("timestamp", System.currentTimeMillis().toString())
                    .build()

                val request = Request.Builder()
                    .url("$SERVER_URL/api/audio.php")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "No error details"
                        throw IOException("Upload failed: ${response.code} - $errorBody")
                    }
                    Log.d("DataUploader", "✅ Audio uploaded successfully: ${audioFile.name} (attempt ${attempt + 1})")
                    return  // Success, exit function
                }
            } catch (e: Exception) {
                lastException = e
                Log.w("DataUploader", "⚠️ Audio upload attempt ${attempt + 1} failed: ${e.message}")

                // Wait before retry (exponential backoff)
                if (attempt < 2) {
                    Thread.sleep((attempt + 1) * 2000L)  // 2s, 4s (audio is larger)
                }
            }
        }

        // All attempts failed
        Log.e("DataUploader", "❌ Audio upload failed after 3 attempts: ${audioFile.name}", lastException)
        throw lastException ?: IOException("Upload failed")
    }

    /**
     * Test server connectivity
     */
    fun testConnection(): Boolean {
        return try {
            val request = Request.Builder()
                .url("$SERVER_URL/api/health.php")  // Use .php for PHP server
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e("DataUploader", "Connection test failed", e)
            false
        }
    }
}