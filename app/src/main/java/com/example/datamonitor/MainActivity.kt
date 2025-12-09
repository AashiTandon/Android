package com.example.datamonitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.datamonitor.services.AudioRecordingService
import com.example.datamonitor.services.LocationService

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private val PERMISSION_REQUEST_CODE = 100

    private val requiredPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.INTERNET
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        // Show privacy warning
        showPrivacyWarning()

        startButton.setOnClickListener {
            if (checkPermissions()) {
                startMonitoring()
            } else {
                requestPermissions()
            }
        }

        stopButton.setOnClickListener {
            stopMonitoring()
        }

        updateUI()
    }

    private fun showPrivacyWarning() {
        AlertDialog.Builder(this)
            .setTitle("Privacy Notice")
            .setMessage("This app will collect location data and audio recordings. " +
                    "Only use this app with explicit consent from the device owner. " +
                    "Unauthorized surveillance is illegal.")
            .setPositiveButton("I Understand") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkPermissions(): Boolean {
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            
            if (allGranted) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
                startMonitoring()
            } else {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show()
                
                // Check if we should show rationale
                val shouldShowRationale = permissions.any { 
                    ActivityCompat.shouldShowRequestPermissionRationale(this, it)
                }
                
                if (!shouldShowRationale) {
                    // User selected "Don't ask again", guide them to settings
                    showSettingsDialog()
                }
            }
            updateUI()
        }
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("This app requires all permissions to function. Please enable them in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startMonitoring() {
        if (!checkPermissions()) {
            Toast.makeText(this, "Missing permissions", Toast.LENGTH_SHORT).show()
            return
        }

        // Start Location Service
        val locationIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(locationIntent)
        } else {
            startService(locationIntent)
        }

        // Start Audio Recording Service
        val audioIntent = Intent(this, AudioRecordingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(audioIntent)
        } else {
            startService(audioIntent)
        }

        Toast.makeText(this, "Monitoring started", Toast.LENGTH_SHORT).show()
        updateUI()
    }

    private fun stopMonitoring() {
        stopService(Intent(this, LocationService::class.java))
        stopService(Intent(this, AudioRecordingService::class.java))
        Toast.makeText(this, "Monitoring stopped", Toast.LENGTH_SHORT).show()
        updateUI()
    }

    private fun updateUI() {
        val hasPermissions = checkPermissions()
        startButton.isEnabled = true
        stopButton.isEnabled = true
        
        if (hasPermissions) {
            statusText.text = "Status: Ready\nPermissions: Granted"
        } else {
            statusText.text = "Status: Waiting for permissions\nPermissions: Not granted"
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}
