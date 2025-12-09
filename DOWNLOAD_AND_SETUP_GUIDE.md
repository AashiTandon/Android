# Download and Setup Guide - Fresh Install

Perfect approach! Starting fresh avoids all cache issues.

---

## ✅ Your Steps Are CORRECT!

Follow these steps exactly:

---

## 📥 STEP 1: Delete Local Project

1. **Close Android Studio** completely
2. **Delete the entire `/workspace` folder** from your computer
3. This clears ALL cache and configuration

---

## 📦 STEP 2: Download Fresh from GitHub

### Option A: Download ZIP

1. Go to your GitHub repository
2. Click green **"Code"** button
3. Click **"Download ZIP"**
4. Save to your Downloads folder

### Option B: Git Clone (if you use git)

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
cd YOUR_REPO_NAME
```

---

## 📂 STEP 3: Extract the ZIP

1. **Right-click** the downloaded ZIP file
2. **Extract All** (Windows) or **Extract** (Mac)
3. Choose a location (e.g., `C:\Projects\` or `~/Projects/`)
4. **Rename** the extracted folder to `DataMonitor` (optional, for clarity)

---

## 🔧 STEP 4: Configure Server URL (IMPORTANT!)

**Before opening in Android Studio**, you need to set your server URL:

1. Navigate to:
   ```
   DataMonitor/app/src/main/java/com/example/datamonitor/network/DataUploader.kt
   ```

2. Open in any text editor (Notepad, VS Code, etc.)

3. Find line 18:
   ```kotlin
   private const val SERVER_URL = "http://YOUR_DOMAIN_HERE"
   ```

4. Change to YOUR actual domain:
   ```kotlin
   private const val SERVER_URL = "http://yourdomain.com"
   ```
   
   **Examples:**
   - `"http://mysite.com"` - if server in root
   - `"http://mysite.com/data-monitor"` - if in subfolder
   - `"https://mysite.com"` - if you have SSL

5. **Save the file**

---

## 🚀 STEP 5: Open in Android Studio

1. **Open Android Studio**

2. **File → Open**

3. **Select** the extracted `DataMonitor` folder

4. **Click OK**

5. If asked **"Trust Project?"** → Click **"Trust"**

---

## ⏳ STEP 6: Wait for Gradle Sync

**THIS IS THE MOST IMPORTANT STEP!**

⏳ **Wait 10-15 minutes**

You'll see at bottom:
```
Downloading gradle-7.5-bin.zip...
Resolving dependencies...
Building...
```

**DO NOT:**
- ❌ Click anything
- ❌ Close Android Studio
- ❌ Disconnect internet
- ❌ Try to build yet

**Wait for:**
```
✓ BUILD SUCCESSFUL
```

---

## ✅ STEP 7: Verify Success

After sync completes, check:

### 1. No Errors
- AndroidManifest.xml should have no red underlines
- All .kt files should open without errors

### 2. Build Works
- **Build → Make Project** (Ctrl+F9 or Cmd+F9)
- Should show: **"BUILD SUCCESSFUL"**

### 3. Run Button Enabled
- Green ▶ button at top should be clickable

---

## 📱 STEP 8: Install on Android Device

1. **Connect your Android device** via USB
   - USB debugging must be enabled

2. **Click Run** (green ▶ button)

3. **Select your device** from list

4. **Wait for installation** (1-2 minutes)

5. **App opens automatically** on your phone

---

## 🎯 STEP 9: Grant Permissions

On your Android device:

1. **Read privacy warning** → Tap "I Understand"

2. **Tap "Start Monitoring"**

3. **Grant permissions:**
   - ✅ Location → "Allow all the time"
   - ✅ Microphone → "Allow"
   - ✅ Files → "Allow"

4. **Check notifications:**
   - Should see "Location Tracking Active"
   - Should see "Audio Recording Active"

---

## 🧪 STEP 10: Verify It's Working

### Check 1: Phone Notifications
Look for two persistent notifications on phone

### Check 2: Server Stats
In browser, visit:
```
http://yourdomain.com/api/stats.php
```

Wait 2-3 minutes, then refresh. Should show:
```json
{
  "locations": 2,
  "audioRecordings": 1,
  "totalAudioSizeMB": "8.45"
}
```

### Check 3: cPanel Data Folder
In cPanel → File Manager:
```
your-upload-location/data/
  ├── locations/  (should have .json files)
  └── audio/      (should have .pcm files)
```

---

## ✅ Advantages of Fresh Download

✅ **No cache issues** - Clean slate  
✅ **No configuration conflicts** - Fresh config  
✅ **All fixes included** - Latest code  
✅ **Easier troubleshooting** - Known state  
✅ **Faster sync** - No old files to clean  

---

## ⚠️ Don't Forget!

Before opening in Android Studio:

1. ✅ **Set SERVER_URL** in DataUploader.kt
2. ✅ **Upload PHP server** to cPanel (if not done yet)
3. ✅ **Test server** in browser first

---

## 📁 What's Included in Download

```
DataMonitor/
├── app/                           # Android app
│   ├── src/main/
│   │   ├── AndroidManifest.xml   # ✅ In correct location
│   │   ├── java/.../
│   │   │   ├── MainActivity.kt
│   │   │   ├── services/
│   │   │   │   ├── LocationService.kt
│   │   │   │   └── AudioRecordingService.kt
│   │   │   └── network/
│   │   │       └── DataUploader.kt  # ← Edit this!
│   │   └── res/
│   │       ├── layout/
│   │       └── values/
│   │           ├── strings.xml
│   │           ├── colors.xml     # ✅ Added
│   │           └── themes.xml     # ✅ Added
│   └── build.gradle               # ✅ Fixed
├── server-php/                    # PHP server for cPanel
│   ├── api/
│   │   ├── health.php
│   │   ├── location.php
│   │   ├── audio.php
│   │   ├── stats.php
│   │   └── recent.php
│   ├── .htaccess
│   └── index.php
├── build.gradle                   # ✅ Fixed (Groovy)
├── settings.gradle                # ✅ Fixed (Groovy)
├── gradle/wrapper/
│   └── gradle-wrapper.properties  # ✅ Gradle 7.5
└── Documentation files...
```

---

## 🔍 Server Setup Reminder

If you haven't uploaded the PHP server yet:

1. **ZIP** the `server-php` folder from downloaded files
2. **Upload** to cPanel File Manager
3. **Extract** in `public_html/`
4. **Test**: `http://yourdomain.com/api/health.php`

See: `START_HERE_PHP.txt` for details

---

## ⏱️ Total Time Estimate

- Delete old project: 1 min
- Download ZIP: 1 min
- Extract: 1 min
- Edit SERVER_URL: 2 min
- Open in Android Studio: 1 min
- Gradle sync: 10-15 min
- Build & install: 3 min
- Test: 2 min
- **Total: 21-26 minutes**

---

## 💡 Why This Approach Works

1. ✅ **No old cache** interfering
2. ✅ **Fresh Gradle wrapper** download
3. ✅ **Clean dependency resolution**
4. ✅ **No configuration conflicts**
5. ✅ **Known working state**

---

## 🆘 If You Get Errors After Fresh Download

### "SDK not found"
→ File → Project Structure → SDK Location  
→ Set Android SDK path (usually auto-detected)

### "Gradle sync failed"
→ Check internet connection  
→ Wait and try again (might be network issue)

### "Kotlin plugin not found"
→ File → Settings → Plugins  
→ Install "Kotlin" plugin  
→ Restart Android Studio

### Still have DependencyHandler error
This shouldn't happen with fresh download, but if it does:
→ File → Invalidate Caches / Restart  
→ Select "Invalidate and Restart"

---

## ✨ Expected Result

After following all steps:

✅ Android Studio opens without errors  
✅ Gradle sync completes successfully  
✅ App builds without issues  
✅ App installs on device  
✅ Monitoring starts working  
✅ Data appears in cPanel  

---

## 📞 Quick Checklist

Before opening in Android Studio:
- [ ] Old project deleted
- [ ] Fresh ZIP downloaded
- [ ] ZIP extracted
- [ ] SERVER_URL configured in DataUploader.kt
- [ ] PHP server uploaded to cPanel (if not done)
- [ ] Server tested in browser

After opening in Android Studio:
- [ ] Project opened
- [ ] Gradle sync started automatically
- [ ] Waited patiently for completion
- [ ] No errors shown
- [ ] BUILD SUCCESSFUL

Ready to install:
- [ ] Android device connected
- [ ] USB debugging enabled
- [ ] Clicked Run button
- [ ] App installed
- [ ] Permissions granted

---

## 🎉 You're Ready!

This fresh download approach will definitely work. 

**The code is now committed and ready to download.**

Just remember to set your SERVER_URL before opening in Android Studio!

Good luck! 🚀
