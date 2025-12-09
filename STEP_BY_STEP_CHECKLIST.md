# ✅ Step-by-Step Checklist: cPanel + Android Studio

Follow this checklist in order. Check off each step as you complete it.

---

## 📋 PART 1: Prepare Server Files (5 minutes)

### On Your Computer:

- [ ] **Step 1.1**: Open terminal/command prompt
- [ ] **Step 1.2**: Navigate to the project:
  ```bash
  cd /workspace
  ```
- [ ] **Step 1.3**: Create ZIP file:
  - **Windows**: Right-click `server` folder → "Send to" → "Compressed folder"
  - **Mac/Linux**: Run `zip -r data-monitor-server.zip server/`
- [ ] **Step 1.4**: Save the ZIP file somewhere easy to find

---

## 📋 PART 2: Deploy to cPanel (10-15 minutes)

### In Your Web Browser:

- [ ] **Step 2.1**: Login to your cPanel
  - URL: Usually `yourdomain.com/cpanel` or `yourdomain.com:2083`

- [ ] **Step 2.2**: Check if Node.js is available
  - Search for "Node" in cPanel search bar
  - Look for "Setup Node.js App" or "Node.js Selector"
  - ✅ If found → Continue
  - ❌ If NOT found → Contact hosting provider to enable it

- [ ] **Step 2.3**: Open File Manager
  - Click "File Manager" icon
  - Navigate to `public_html` folder

- [ ] **Step 2.4**: Create a new folder
  - Click "New Folder" button
  - Name it: `data-monitor`
  - Click "Create New Folder"

- [ ] **Step 2.5**: Enter the folder
  - Double-click on `data-monitor` folder

- [ ] **Step 2.6**: Upload the ZIP file
  - Click "Upload" button
  - Click "Select File"
  - Choose `data-monitor-server.zip`
  - Wait for "100% complete"
  - Close upload window

- [ ] **Step 2.7**: Extract the ZIP
  - Find the ZIP file in file list
  - Right-click → "Extract"
  - Click "Extract Files"
  - Click "Close"

- [ ] **Step 2.8**: Organize files
  - You should now see a `server` folder
  - Enter the `server` folder
  - Select all files (Ctrl+A or Cmd+A)
  - Click "Move"
  - Change path from `/public_html/data-monitor/server` to `/public_html/data-monitor`
  - Click "Move Files"
  - Go back to `data-monitor` folder
  - Delete empty `server` folder
  - Delete the ZIP file

- [ ] **Step 2.9**: Verify files are in place
  - In `data-monitor` folder, you should see:
    - `server.js`
    - `package.json`
    - `.gitignore` (optional)

- [ ] **Step 2.10**: Go back to cPanel home
  - Click "Home" or cPanel logo

- [ ] **Step 2.11**: Setup Node.js Application
  - Click "Setup Node.js App"
  - Click "Create Application" button

- [ ] **Step 2.12**: Fill in the form:
  - **Node.js version**: Select latest (18.x or higher)
  - **Application mode**: Production
  - **Application root**: Type `data-monitor`
  - **Application URL**: Select your domain
  - **Application startup file**: Type `server.js`
  - Click "Create" button

- [ ] **Step 2.13**: Install dependencies
  - cPanel will show a command like: `source /home/username/nodevenv/.../bin/activate && cd /home/.../data-monitor`
  - Click "Run NPM Install" button (if available)
  - OR open Terminal (in cPanel)
  - Run these commands:
    ```bash
    cd data-monitor
    npm install
    ```
  - Wait for installation (1-2 minutes)

- [ ] **Step 2.14**: Start the application
  - In "Setup Node.js App", find your app
  - Click the play/start button
  - Status should show "Running" with green indicator

- [ ] **Step 2.15**: Note your server URL
  - cPanel will show something like:
    - `http://yourdomain.com`
    - OR `http://yourdomain.com:PORT`
  - **Write it down**: ________________________________

- [ ] **Step 2.16**: Test the server
  - Open new browser tab
  - Go to: `http://yourdomain.com/api/health` (or with PORT if shown)
  - You should see JSON response:
    ```json
    {
      "status": "ok",
      "message": "Server is running",
      "timestamp": "..."
    }
    ```
  - ✅ If you see this → Server working!
  - ❌ If error → Check troubleshooting in CPANEL_DEPLOYMENT_GUIDE.md

---

## 📋 PART 3: Configure Android App (10 minutes)

### In Android Studio:

- [ ] **Step 3.1**: Launch Android Studio

- [ ] **Step 3.2**: Open the project
  - File → Open
  - Navigate to `/workspace` folder
  - Click "OK"
  - Wait for "Gradle sync" to complete (status at bottom)
  - May take 5-10 minutes first time

- [ ] **Step 3.3**: Navigate to DataUploader file
  - On left side, expand folders:
    - `app`
    - `src`
    - `main`
    - `java`
    - `com.example.datamonitor`
    - `network`
  - Click on `DataUploader.kt`

- [ ] **Step 3.4**: Update server URL
  - Find line (around line 14):
    ```kotlin
    private const val SERVER_URL = "http://YOUR_SERVER_IP:3000"
    ```
  - Replace with YOUR server URL from Step 2.15
  - Example:
    ```kotlin
    private const val SERVER_URL = "http://yourdomain.com"
    ```
  - Save file (Ctrl+S or Cmd+S)

- [ ] **Step 3.5**: Build the project
  - Build → Make Project
  - OR press Ctrl+F9 (Windows/Linux) or Cmd+F9 (Mac)
  - Wait for "BUILD SUCCESSFUL" message at bottom

---

## 📋 PART 4: Prepare Android Device (5 minutes)

### On Your Android Phone:

- [ ] **Step 4.1**: Enable Developer Options
  - Go to Settings
  - Scroll to "About Phone" or "System"
  - Find "Build Number"
  - Tap it 7 times rapidly
  - You'll see "You are now a developer!"

- [ ] **Step 4.2**: Enable USB Debugging
  - Go back to Settings
  - Find "Developer Options" (might be under System)
  - Turn on "USB Debugging"
  - Confirm if prompted

- [ ] **Step 4.3**: Connect phone to computer
  - Use USB cable
  - Make sure it's a data cable (not just charging)

- [ ] **Step 4.4**: Allow USB Debugging
  - On phone, you'll see prompt: "Allow USB debugging?"
  - Check "Always allow from this computer"
  - Tap "OK"

- [ ] **Step 4.5**: Verify connection
  - In Android Studio, look at top toolbar
  - Device dropdown should show your phone model
  - ✅ If shown → Connected!
  - ❌ If not shown → Try different USB cable or port

---

## 📋 PART 5: Install and Run App (5 minutes)

### In Android Studio:

- [ ] **Step 5.1**: Select your device
  - Top toolbar, device dropdown
  - Select your phone from list

- [ ] **Step 5.2**: Run the app
  - Click green "Run" button (▶️)
  - OR press Shift+F10

- [ ] **Step 5.3**: Wait for installation
  - Progress bar will show at bottom
  - "Installing APKs"
  - May take 1-2 minutes

- [ ] **Step 5.4**: App launches on phone
  - App should open automatically
  - ✅ If opened → Success!
  - ❌ If error → Check Logcat at bottom of Android Studio

---

## 📋 PART 6: Grant Permissions (5 minutes)

### On Your Android Phone:

- [ ] **Step 6.1**: Read privacy warning
  - App shows privacy notice
  - Read it carefully
  - Tap "I Understand"

- [ ] **Step 6.2**: Tap "Start Monitoring"

- [ ] **Step 6.3**: Grant Location permission
  - Popup: "Allow Data Monitor to access this device's location?"
  - **Important**: Tap "Allow all the time" or "Allow"
  - (Not "Only while using the app")

- [ ] **Step 6.4**: Grant Microphone permission
  - Popup: "Allow Data Monitor to record audio?"
  - Tap "Allow"

- [ ] **Step 6.5**: Grant Files permission (if prompted)
  - Popup: "Allow Data Monitor to access photos and media?"
  - Tap "Allow"

- [ ] **Step 6.6**: Grant Background Location (Android 10+)
  - May show additional prompt
  - Tap "Allow all the time"

- [ ] **Step 6.7**: Check if permissions fully granted
  - If any permission was denied, grant manually:
  - Settings → Apps → Data Monitor → Permissions
  - Turn on ALL permissions

- [ ] **Step 6.8**: Disable battery optimization
  - Settings → Battery → Battery optimization
  - Find "Data Monitor"
  - Select "Don't optimize"

---

## 📋 PART 7: Verify It's Working (5 minutes)

### Check 1: Phone Notifications

- [ ] **Step 7.1**: Look at phone notification bar
  - You should see TWO ongoing notifications:
    - "Location Tracking Active"
    - "Audio Recording Active"
  - ✅ If you see both → App is running!

### Check 2: Server Statistics

- [ ] **Step 7.2**: In web browser, go to:
  ```
  http://yourdomain.com/api/stats
  ```
  (Replace with your URL from Step 2.15)

- [ ] **Step 7.3**: Wait 2-3 minutes, then refresh page

- [ ] **Step 7.4**: Check the numbers:
  ```json
  {
    "locations": 2,        ← Should be increasing
    "audioRecordings": 1,  ← Should be increasing
    "totalAudioSizeMB": "5.23"
  }
  ```
  - ✅ If numbers increase → **WORKING!**

### Check 3: View Data Files

- [ ] **Step 7.5**: Go to cPanel → File Manager

- [ ] **Step 7.6**: Navigate to:
  ```
  data-monitor/data/
  ```

- [ ] **Step 7.7**: Check folders:
  - `locations/` folder → Should contain .json files
  - `audio/` folder → Should contain .pcm files
  - ✅ If files exist → Data is being saved!

### Check 4: Android Studio Logs

- [ ] **Step 7.8**: In Android Studio, click "Logcat" tab (bottom)

- [ ] **Step 7.9**: In search box, type: `DataUploader`

- [ ] **Step 7.10**: Look for messages like:
  ```
  Location uploaded: 37.7749, -122.4194
  Audio uploaded: audio_20251128_143045.pcm
  ```
  - ✅ If you see these → Upload working!

---

## 🎉 CONGRATULATIONS!

If all checks passed, your monitoring system is fully operational!

### What's Happening Now:

✅ Your Android device is tracking GPS location every 60 seconds  
✅ Recording surrounding audio in 60-second segments  
✅ Uploading all data to your cPanel server  
✅ Data is being saved and can be accessed anytime  

---

## 🔴 Troubleshooting

If something didn't work, check the specific issue:

### Issue: "Can't find Node.js in cPanel"
→ See: CPANEL_DEPLOYMENT_GUIDE.md → "Alternative: If cPanel Doesn't Support Node.js"

### Issue: "Health check returns error"
→ See: CPANEL_DEPLOYMENT_GUIDE.md → "Troubleshooting → Server Not Accessible"

### Issue: "App crashes immediately"
→ In Android Studio → Logcat → Look for red error messages
→ Common fix: Build → Clean Project → Rebuild Project

### Issue: "Permissions not granted"
→ Go to phone: Settings → Apps → Data Monitor → Permissions
→ Manually enable ALL permissions

### Issue: "No data in cPanel"
→ Check Android Studio Logcat for upload errors
→ Verify SERVER_URL is exactly correct in DataUploader.kt
→ Make sure phone has internet connection

### Issue: "Services stop after a while"
→ Disable battery optimization (Step 6.8)
→ Some phone brands (Xiaomi, Huawei) need additional settings

---

## 📱 How to Stop Monitoring

When you want to stop:

1. Open the app
2. Tap "Stop Monitoring"
3. Notifications will disappear

To start again:
1. Open the app
2. Tap "Start Monitoring"

---

## 📊 How to View Collected Data

### View Statistics:
```
http://yourdomain.com/api/stats
```

### View Recent Locations:
```
http://yourdomain.com/api/locations/recent?limit=10
```

### Download Files:
- cPanel → File Manager
- Navigate to `data-monitor/data/`
- Right-click any file → Download

---

## ⚠️ FINAL REMINDER

**This app collects highly sensitive data.**

✅ **Always** obtain consent  
✅ **Only** use legally  
✅ **Secure** your server  
✅ **Comply** with all laws  

Unauthorized surveillance is illegal and has serious consequences.

---

## 📚 Next Steps

Once everything is working:

- [ ] Enable HTTPS in cPanel (SSL/TLS)
- [ ] Update Android app to use https://
- [ ] Set up data backup system
- [ ] Configure automatic data cleanup
- [ ] Review security in DEPLOYMENT.md

---

## 📞 Need More Help?

- **Detailed cPanel guide**: CPANEL_DEPLOYMENT_GUIDE.md
- **Android Studio issues**: README.md → Troubleshooting
- **Server issues**: server/README.md
- **Legal questions**: PRIVACY_WARNING.md

---

**You've got this! 🚀**
