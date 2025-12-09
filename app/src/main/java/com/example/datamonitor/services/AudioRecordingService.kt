package com.example.datamonitor.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.datamonitor.R
import com.example.datamonitor.network.DataUploader
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AudioRecordingService : Service() {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var recordingJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "AudioRecordingChannel"
        private const val NOTIFICATION_ID = 2
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val RECORDING_DURATION = 60000L // 1 minute segments
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startRecording()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Recording",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Records audio in background"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Audio Recording Active")
            .setContentText("Recording audio...")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startRecording() {
        recordingJob = serviceScope.launch {
            while (isActive) {
                try {
                    recordAudioSegment()
                    delay(RECORDING_DURATION)
                } catch (e: Exception) {
                    android.util.Log.e("AudioRecordingService", "Recording error", e)
                    delay(5000) // Wait before retry
                }
            }
        }
    }

    private suspend fun recordAudioSegment() = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            android.util.Log.e("AudioRecordingService", "Invalid buffer size")
            return@withContext
        }

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                android.util.Log.e("AudioRecordingService", "AudioRecord not initialized")
                return@withContext
            }

            // Create temp file
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val audioFile = File(cacheDir, "audio_$timestamp.pcm")
            val outputStream = FileOutputStream(audioFile)

            audioRecord?.startRecording()
            isRecording = true

            val audioData = ByteArray(bufferSize)
            val endTime = System.currentTimeMillis() + RECORDING_DURATION

            while (isRecording && System.currentTimeMillis() < endTime && isActive) {
                val readSize = audioRecord?.read(audioData, 0, audioData.size) ?: 0
                if (readSize > 0) {
                    outputStream.write(audioData, 0, readSize)
                }
            }

            outputStream.close()
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
            isRecording = false

            // Upload audio file
            try {
                DataUploader.uploadAudio(audioFile)
                android.util.Log.d("AudioRecordingService", "✅ Audio uploaded: ${audioFile.name}")
                // Only delete file after SUCCESSFUL upload
                audioFile.delete()
                android.util.Log.d("AudioRecordingService", "🗑️ Temporary file deleted: ${audioFile.name}")
            } catch (e: Exception) {
                android.util.Log.e("AudioRecordingService", "❌ Failed to upload audio: ${audioFile.name}", e)
                android.util.Log.w("AudioRecordingService", "⚠️ File kept for potential retry: ${audioFile.absolutePath}")
                // Don't delete file - keep it for potential manual recovery
                // Note: You may want to implement a cleanup of old failed files
            }

        } catch (e: SecurityException) {
            android.util.Log.e("AudioRecordingService", "Permission denied", e)
        } catch (e: Exception) {
            android.util.Log.e("AudioRecordingService", "Recording failed", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        serviceScope.cancel()
    }
}