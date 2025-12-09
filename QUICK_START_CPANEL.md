# Quick Start: cPanel + Android Studio

Ultra-short version for experienced users.

---

## Server Setup (cPanel)

### 1. Upload Files

```bash
# Zip the server folder
cd /workspace
zip -r data-monitor-server.zip server/
```

- Upload to cPanel File Manager
- Extract to a folder (e.g., `data-monitor`)

### 2. Setup Node.js App in cPanel

1. cPanel → "Setup Node.js App"
2. Create Application:
   - Application root: `data-monitor`
   - Startup file: `server.js`
   - Node version: 18.x

3. Install dependencies:
```bash
cd data-monitor
npm install
```

4. Start the app in cPanel interface

5. Test: `http://yourdomain.com/api/health`

---

## Android App Setup (Android Studio)

### 1. Open Project

- Open `/workspace` in Android Studio
- Wait for Gradle sync

### 2. Configure Server URL

Edit: `app/src/main/java/com/example/datamonitor/network/DataUploader.kt`

```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

### 3. Build & Run

1. Connect Android device (enable USB debugging)
2. Click Run (green play button)
3. Grant all permissions
4. Tap "Start Monitoring"

---

## Verify It's Working

**Check stats:**
```
http://yourdomain.com/api/stats
```

**Check data folder in cPanel:**
```
data-monitor/data/locations/
data-monitor/data/audio/
```

**Check Android Studio Logcat:**
```
Filter: DataUploader
Look for: "Location uploaded" or "Audio uploaded"
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Can't connect | Test `curl http://yourdomain.com/api/health` |
| App crashes | Check Logcat, rebuild project |
| No permissions | Grant manually in Settings → Apps |
| 404 error | Check app is running in cPanel |

---

## Important Files

**Android (change server URL):**
```
app/src/main/java/com/example/datamonitor/network/DataUploader.kt
```

**Server (if you need to edit):**
```
data-monitor/server.js
```

---

## Enable HTTPS (Recommended)

1. cPanel → SSL/TLS Status
2. Enable AutoSSL
3. Change Android app URL to `https://yourdomain.com`

---

Done! 🎉
