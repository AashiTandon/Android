# Quick Setup Guide

## Step 1: Set Up the Server

### Prerequisites
- Node.js 16+ installed
- A computer/server with a static IP or accessible hostname

### Installation Steps

1. **Navigate to server directory**:
```bash
cd server
```

2. **Install dependencies**:
```bash
npm install
```

3. **Start the server**:
```bash
npm start
```

4. **Note your server IP address**:
```bash
# On Linux/Mac
ip addr show | grep inet

# On Windows
ipconfig
```

Your server is now running on `http://YOUR_IP:3000`

### Test the Server

```bash
curl http://localhost:3000/api/health
```

Expected response:
```json
{
  "status": "ok",
  "message": "Server is running",
  "timestamp": "2025-11-28T..."
}
```

---

## Step 2: Configure the Android App

### Prerequisites
- Android Studio (latest version)
- Android device with USB debugging enabled
- OR Android emulator

### Configuration Steps

1. **Open the project in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to `/workspace` directory

2. **Wait for Gradle sync** to complete (first time may take a few minutes)

3. **Update Server URL**:
   - Open: `app/src/main/java/com/example/datamonitor/network/DataUploader.kt`
   - Find line: `private const val SERVER_URL = "http://YOUR_SERVER_IP:3000"`
   - Replace `YOUR_SERVER_IP` with your actual server IP address
   - Example: `private const val SERVER_URL = "http://192.168.1.100:3000"`

4. **Important Network Configuration**:
   - If testing on emulator, use `10.0.2.2` instead of `localhost`
   - If testing on physical device, ensure device and server are on same network
   - For internet-accessible server, use public IP or domain name

---

## Step 3: Build and Install the App

### Option A: Using Android Studio (Recommended)

1. **Connect your Android device** via USB or start emulator
2. **Click the "Run" button** (green triangle) in Android Studio
3. **Select your device** from the list
4. Wait for build and installation to complete

### Option B: Build APK Manually

1. **Build the APK**:
```bash
cd /workspace
./gradlew assembleDebug
```

2. **Find the APK**:
```
app/build/outputs/apk/debug/app-debug.apk
```

3. **Install on device**:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Step 4: Grant Permissions and Start Monitoring

### On the Android Device

1. **Launch the app** "Data Monitor"

2. **Read and acknowledge** the privacy warning

3. **Tap "Start Monitoring"**

4. **Grant permissions** when prompted:
   - Location (Always)
   - Microphone
   - Files and media
   - Allow background location

5. **Verify it's running**:
   - You should see persistent notifications
   - "Location Tracking Active"
   - "Audio Recording Active"

---

## Step 5: Verify Data Collection

### Check Server Logs

Your server console should show incoming data:
```
📍 Location received: { lat: 37.7749, lng: -122.4194, ... }
🎤 Audio received: { filename: 'audio_20251128_143045.pcm', ... }
```

### View Statistics

```bash
curl http://YOUR_SERVER_IP:3000/api/stats
```

Expected response:
```json
{
  "locations": 15,
  "audioRecordings": 3,
  "totalAudioSizeMB": "25.67",
  "dataDirectory": "/path/to/server/data"
}
```

### View Recent Locations

```bash
curl http://YOUR_SERVER_IP:3000/api/locations/recent?limit=5
```

---

## Troubleshooting

### "Cannot connect to server"

**Problem**: App can't reach the server

**Solutions**:
1. Verify server is running: `curl http://localhost:3000/api/health`
2. Check firewall settings (allow port 3000)
3. Ensure device and server are on same network
4. If using emulator, use `10.0.2.2` instead of `localhost`
5. Check server IP address is correct in `DataUploader.kt`

### "Permission denied"

**Problem**: App doesn't have required permissions

**Solutions**:
1. Go to: Settings > Apps > Data Monitor > Permissions
2. Manually enable all permissions
3. For background location (Android 10+):
   - Select "Allow all the time" for Location permission

### "Audio not recording"

**Problem**: No audio files on server

**Solutions**:
1. Check microphone permission is granted
2. Ensure no other app is using microphone
3. Check app logs in Android Studio (View > Tool Windows > Logcat)
4. Look for "AudioRecordingService" errors

### "Location not updating"

**Problem**: GPS not working

**Solutions**:
1. Enable Location/GPS on device
2. Grant "Allow all the time" for location permission
3. Disable battery optimization for the app:
   - Settings > Battery > Battery optimization
   - Select "All apps"
   - Find "Data Monitor" > Don't optimize

---

## Network Configuration

### For Local Network Testing

1. Server and device on same WiFi network
2. Use local IP address (e.g., `192.168.1.100`)
3. Make sure router allows inter-device communication

### For Internet Access

1. **Port forwarding** on router (port 3000)
2. **Use public IP** or domain name
3. **Consider security**: Add authentication, use HTTPS
4. **Firewall rules**: Allow incoming connections on port 3000

### For Production

1. **Get SSL certificate** (Let's Encrypt)
2. **Use HTTPS** instead of HTTP
3. **Implement authentication** (API keys, JWT)
4. **Set up proper server** (not just your laptop)
5. **Use environment variables** for configuration
6. **Implement rate limiting**

---

## File Locations

### Server Data

```
server/
└── data/
    ├── locations/
    │   ├── location_2025-11-28T14-30-45.json
    │   ├── locations_2025-11-28.jsonl
    │   └── ...
    └── audio/
        ├── audio_20251128_143045.pcm
        ├── audio_20251128_143045_metadata.json
        └── ...
```

### Android APK

```
app/build/outputs/apk/debug/app-debug.apk
```

---

## Monitoring Tips

### Battery Optimization

Continuous location and audio recording will drain battery quickly:
- Keep device plugged in for long-term monitoring
- Consider reducing location update frequency
- Adjust audio recording duration

### Storage Management

Audio files can grow large:
- 1 minute of PCM audio ≈ 5-10 MB
- Monitor server disk space
- Implement automatic cleanup of old files

### Privacy Considerations

**NEVER FORGET**:
- Always obtain consent
- Inform users what data is collected
- Secure the server properly
- Delete data when no longer needed
- Comply with all applicable laws

---

## Next Steps

Once everything is working:

1. ✅ Verify data is being collected correctly
2. ✅ Review privacy compliance
3. ✅ Implement proper security measures
4. ✅ Set up HTTPS if needed
5. ✅ Configure data retention policies
6. ✅ Test battery life and performance
7. ✅ Document your usage policy

---

## Getting Help

### Check Logs

**Server logs**: Check terminal where server is running

**Android logs**:
```bash
adb logcat | grep -E "(LocationService|AudioRecordingService|DataUploader)"
```

### Common Error Messages

| Error | Meaning | Solution |
|-------|---------|----------|
| `ECONNREFUSED` | Server not reachable | Check server is running, verify IP |
| `SecurityException` | Missing permission | Grant all permissions in Settings |
| `Upload failed: 500` | Server error | Check server logs for details |
| `AudioRecord not initialized` | Mic unavailable | Close apps using microphone |

---

## Support Checklist

Before asking for help, verify:

- [ ] Server is running and accessible
- [ ] Server URL is correctly configured in app
- [ ] All permissions are granted
- [ ] Device has internet connectivity
- [ ] Firewall allows connections
- [ ] Battery optimization is disabled for app
- [ ] No other app is using microphone
- [ ] GPS/Location is enabled on device

---

## Quick Test Script

Test the complete setup:

```bash
#!/bin/bash

echo "Testing Data Monitor Setup..."

# Test 1: Server health
echo "1. Testing server health..."
curl -s http://YOUR_SERVER_IP:3000/api/health && echo "✅ Server OK" || echo "❌ Server unreachable"

# Test 2: Location endpoint
echo "2. Testing location endpoint..."
curl -s -X POST http://YOUR_SERVER_IP:3000/api/location \
  -H "Content-Type: application/json" \
  -d '{"latitude":37.7749,"longitude":-122.4194,"accuracy":15.5,"altitude":10.0,"speed":0.0,"timestamp":"2025-11-28 14:30:45"}' \
  && echo "✅ Location endpoint OK" || echo "❌ Location endpoint failed"

# Test 3: Stats
echo "3. Getting stats..."
curl -s http://YOUR_SERVER_IP:3000/api/stats

echo "Test complete!"
```

Save as `test_setup.sh`, make executable, and run:
```bash
chmod +x test_setup.sh
./test_setup.sh
```

---

Good luck with your monitoring setup! Remember to use responsibly and legally.
