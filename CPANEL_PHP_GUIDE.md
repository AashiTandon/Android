# Complete cPanel Guide (PHP Version - EASY!)

**No Node.js required! This is MUCH easier for cPanel.**

---

## ✅ What You Need

- cPanel hosting account (any plan with PHP)
- Android Studio on your computer
- Android device
- 30-40 minutes

---

## 📋 PART 1: Upload PHP Server to cPanel (10 minutes)

### Step 1: Prepare Files on Your Computer

1. **Navigate to the PHP server folder**:
   - Location: `/workspace/server-php/`

2. **Create a ZIP file**:

**Windows:**
- Right-click on `server-php` folder
- Select "Send to" → "Compressed (zipped) folder"
- Rename to: `data-monitor.zip`

**Mac:**
- Right-click on `server-php` folder
- Select "Compress server-php"
- Rename to: `data-monitor.zip`

**Linux:**
```bash
cd /workspace
zip -r data-monitor.zip server-php/
```

### Step 2: Upload to cPanel

1. **Login to cPanel**
   - Usually: `yourdomain.com/cpanel` or `yourdomain.com:2083`

2. **Open File Manager**
   - Click on "File Manager" icon

3. **Navigate to location**:
   - Option A: `public_html/` (makes it accessible at `yourdomain.com`)
   - Option B: `public_html/data-monitor/` (accessible at `yourdomain.com/data-monitor`)

4. **Upload the ZIP file**:
   - Click "Upload" button at top
   - Click "Select File"
   - Choose `data-monitor.zip`
   - Wait for upload to complete (100%)
   - Click "Go Back"

5. **Extract the ZIP**:
   - Find `data-monitor.zip` in file list
   - Right-click on it
   - Select "Extract"
   - Click "Extract Files" button
   - Close dialog

6. **Organize files**:
   - You'll see a `server-php` folder
   - Enter it
   - Select ALL files (Ctrl+A)
   - Click "Move"
   - Change path to remove `/server-php` at the end
   - Click "Move Files"
   - Go back one level
   - Delete empty `server-php` folder
   - Delete `data-monitor.zip`

7. **Verify structure**:
   Your files should now be at:
   ```
   public_html/
   ├── api/
   │   ├── health.php
   │   ├── location.php
   │   ├── audio.php
   │   ├── stats.php
   │   └── recent.php
   ├── .htaccess
   └── index.php
   ```

### Step 3: Set Permissions

1. **Still in File Manager**

2. **Select the folder** (where you uploaded files)

3. **Right-click** → "Change Permissions"

4. **Set to 755** (or check: Owner Read/Write/Execute, Group/Public Read/Execute)

5. **Check "Recurse into subdirectories"**

6. **Click "Change Permissions"**

### Step 4: Test the Server

1. **Open browser**

2. **Visit your server URL**:
   - If uploaded to `public_html/`: `http://yourdomain.com/api/health.php`
   - If in subfolder: `http://yourdomain.com/data-monitor/api/health.php`

3. **You should see**:
```json
{
    "status": "ok",
    "message": "Server is running",
    "timestamp": "2025-11-28T14:30:45.123Z",
    "server": "PHP 7.4.33"
}
```

✅ **If you see this → SERVER WORKING!**

4. **Also try visiting**:
   - `http://yourdomain.com/` (or `/data-monitor/`)
   - You should see a nice welcome page with all endpoints

---

## 📋 PART 2: Configure Android App (10 minutes)

### Step 1: Open in Android Studio

1. **Launch Android Studio**

2. **Open project**:
   - File → Open
   - Navigate to `/workspace`
   - Click "OK"

3. **Wait for Gradle sync** (may take 5-10 minutes first time)

### Step 2: Update Server URL

1. **Navigate to**:
   ```
   app → src → main → java → com.example.datamonitor → network → DataUploader.kt
   ```

2. **Find these lines** (around line 14):
```kotlin
private const val SERVER_URL = "http://YOUR_SERVER_IP:3000"
```

3. **Change to your server URL**:

**If files in root of domain:**
```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

**If files in subfolder:**
```kotlin
private const val SERVER_URL = "http://yourdomain.com/data-monitor"
```

4. **Also update API endpoints** (around lines 48-60):

**Find:**
```kotlin
.url("$SERVER_URL/api/location")
```

**Change to:**
```kotlin
.url("$SERVER_URL/api/location.php")
```

**Do the same for audio endpoint:**
```kotlin
.url("$SERVER_URL/api/audio")
```
**Change to:**
```kotlin
.url("$SERVER_URL/api/audio.php")
```

5. **Save file** (Ctrl+S or Cmd+S)

### Step 3: Build the Project

1. **Build → Make Project**
   - Or press `Ctrl+F9` (Win/Linux) or `Cmd+F9` (Mac)

2. **Wait for build to complete**
   - Check bottom status bar
   - Should say "BUILD SUCCESSFUL"

---

## 📋 PART 3: Install App on Android Device (10 minutes)

### Step 1: Prepare Device

1. **Enable Developer Options**:
   - Settings → About Phone
   - Tap "Build Number" 7 times
   - You'll see "You are now a developer!"

2. **Enable USB Debugging**:
   - Settings → Developer Options
   - Turn on "USB Debugging"

3. **Connect to computer** via USB cable

4. **Allow USB debugging** on phone when prompted

### Step 2: Run the App

1. **In Android Studio**, select your device from dropdown (top toolbar)

2. **Click Run button** (green play icon ▶️)
   - Or press `Shift+F10`

3. **Wait for installation** (1-2 minutes)

4. **App opens automatically** on your phone

### Step 3: Grant Permissions

1. **Read privacy warning** → "I Understand"

2. **Tap "Start Monitoring"**

3. **Grant permissions**:
   - ✅ Location → "Allow all the time"
   - ✅ Microphone → "Allow"
   - ✅ Files → "Allow"

4. **Check notifications**:
   - Should see "Location Tracking Active"
   - Should see "Audio Recording Active"

---

## 📋 PART 4: Verify It's Working (5 minutes)

### Check 1: Visit Stats Page

**In browser:**
```
http://yourdomain.com/api/stats.php
```
(or `/data-monitor/api/stats.php` if in subfolder)

**Wait 2-3 minutes, then refresh**

Should see:
```json
{
    "locations": 3,
    "audioRecordings": 1,
    "totalAudioSizeMB": "8.45",
    "dataDirectory": "..."
}
```

✅ **If numbers increase → IT'S WORKING!**

### Check 2: View Files in cPanel

1. **cPanel → File Manager**

2. **Navigate to your upload location**

3. **You should see**:
   - `data/` folder (auto-created)
   - `data/locations/` with .json files
   - `data/audio/` with .pcm files

### Check 3: Android Studio Logs

1. **In Android Studio → Logcat** (bottom tab)

2. **Filter by**: `DataUploader`

3. **Look for**:
   ```
   Location uploaded: 37.7749, -122.4194
   Audio uploaded: audio_20251128_143045.pcm
   ```

---

## 🎉 SUCCESS!

Your monitoring system is now fully operational!

### What's Happening:

✅ Phone tracks location every 60 seconds  
✅ Records audio in 60-second segments  
✅ Uploads everything to your cPanel server  
✅ Data saved and accessible anytime  

---

## 🔧 Troubleshooting

### Issue: "Can't access health.php - 404 error"

**Check:**
1. Files uploaded to correct location
2. URL is correct (include `/api/health.php`)
3. `.htaccess` file is present

**Fix:**
- Re-upload files
- Check URL carefully
- Make sure `.htaccess` uploaded (might be hidden)

### Issue: "500 Internal Server Error"

**Check cPanel Error Log:**
1. cPanel → Metrics → Errors
2. Look for PHP errors

**Common causes:**
- File permissions wrong → Set to 755
- PHP version too old → Contact host
- `.htaccess` syntax error → Re-upload

**Fix permissions:**
```
In cPanel File Manager:
- Select folder
- Right-click → Change Permissions
- Set to 755
- Check "Recurse into subdirectories"
```

### Issue: "Permission denied" when uploading data

**Fix:**
1. cPanel → File Manager
2. Navigate to your server folder
3. Create `data` folder if doesn't exist
4. Right-click `data` folder → Change Permissions → 777
5. Check "Recurse into subdirectories"

### Issue: "File too large" error

**Fix:**
1. Edit `.htaccess` in your server folder
2. Change:
```apache
php_value upload_max_filesize 200M
php_value post_max_size 200M
```

### Issue: App says "Upload failed"

**Check:**
1. Android Studio Logcat for specific error
2. Server URL is exactly correct in DataUploader.kt
3. Phone has internet connection
4. Server endpoints work in browser

**Common mistake:**
- Forgot to add `.php` to endpoints
- Wrong URL (http vs https, domain name, path)

---

## 📊 View Your Data

### Live Statistics Dashboard

Visit in browser:
```
http://yourdomain.com/
```
or
```
http://yourdomain.com/data-monitor/
```

Shows nice dashboard with clickable endpoint links!

### Download Data Files

**cPanel Method:**
1. cPanel → File Manager
2. Navigate to `data/locations/` or `data/audio/`
3. Right-click any file → Download

### View in Browser

**Recent locations:**
```
http://yourdomain.com/api/recent.php?limit=10
```

**Statistics:**
```
http://yourdomain.com/api/stats.php
```

---

## 🔒 Security Improvements

### Enable HTTPS (Recommended)

1. **cPanel → SSL/TLS Status**
2. **Click "Run AutoSSL"**
3. **Wait for certificate installation**
4. **Update Android app**:
```kotlin
private const val SERVER_URL = "https://yourdomain.com"
```

### Add Password Protection

1. **cPanel → Directory Privacy**
2. **Select your folder**
3. **Enable protection**
4. **Create username/password**

---

## 📁 File Locations Reference

**Uploaded to root:**
```
public_html/
├── api/
│   ├── health.php       ← Test: yourdomain.com/api/health.php
│   ├── location.php     ← Receives location data
│   ├── audio.php        ← Receives audio files
│   ├── stats.php        ← Test: yourdomain.com/api/stats.php
│   └── recent.php
├── data/                ← Auto-created, stores all data
│   ├── locations/
│   └── audio/
├── .htaccess            ← Configuration
└── index.php            ← Homepage

SERVER_URL in Android: "http://yourdomain.com"
Endpoints: /api/location.php, /api/audio.php
```

**Uploaded to subfolder:**
```
public_html/
└── data-monitor/
    ├── api/
    │   ├── health.php   ← Test: yourdomain.com/data-monitor/api/health.php
    │   └── ...
    ├── data/
    ├── .htaccess
    └── index.php

SERVER_URL in Android: "http://yourdomain.com/data-monitor"
Endpoints: /api/location.php, /api/audio.php
```

---

## ✅ Final Checklist

- [ ] PHP files uploaded to cPanel
- [ ] Permissions set to 755
- [ ] Health check works in browser
- [ ] SERVER_URL updated in DataUploader.kt
- [ ] Endpoints updated to include `.php`
- [ ] App built successfully
- [ ] App installed on Android device
- [ ] All permissions granted
- [ ] Notifications showing
- [ ] Stats API shows increasing numbers
- [ ] Files appearing in data folder

---

## 📞 Need Help?

**Server not responding:**
- Check cPanel Error Logs
- Verify file permissions (755)
- Test in browser first

**App won't connect:**
- Double-check SERVER_URL
- Make sure you added `.php` to endpoints
- Test server in browser

**No data being saved:**
- Check `data/` folder permissions (777)
- Check cPanel error logs
- View Android Studio Logcat

---

## 🚀 Advantages of PHP Version

✅ **No Node.js setup required**  
✅ **Works on ALL cPanel hosting**  
✅ **No npm install needed**  
✅ **No processes to manage**  
✅ **Easier to debug**  
✅ **Better integrated with cPanel**  
✅ **Immediate setup - just upload and go!**  

---

## ⚠️ Legal Reminder

This app collects:
- GPS location continuously
- Audio recordings continuously

**You MUST:**
- Obtain explicit written consent
- Use only for legal purposes
- Comply with all applicable laws

Read `PRIVACY_WARNING.md` for details.

---

## 🎊 You're Done!

Your system is live and collecting data!

**Next steps:**
- Enable HTTPS
- Set up backups
- Configure data retention
- Review security settings

Good luck! 🚀
