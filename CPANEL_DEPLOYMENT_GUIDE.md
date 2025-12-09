# cPanel Deployment Guide for Data Monitor

Complete guide to deploy the Node.js server on cPanel and connect it with your Android app.

---

## Prerequisites

✅ cPanel hosting account  
✅ cPanel with Node.js support (check with your hosting provider)  
✅ Domain name or subdomain  
✅ Android Studio installed on local machine  
✅ Android device for testing  

---

## Part 1: Deploy Server to cPanel

### Step 1: Check if Your cPanel Supports Node.js

1. **Login to cPanel**
2. **Search for "Node.js"** in the search bar
3. Look for "Setup Node.js App" or "Node.js Selector"

**If you DON'T see Node.js:**
- Contact your hosting provider to enable it
- Or consider upgrading to a plan that supports Node.js
- Alternative: Use a different hosting (see alternatives at end)

### Step 2: Prepare Server Files

1. **On your local machine, navigate to the server directory**:
```bash
cd /workspace/server
```

2. **Create a ZIP file of the server folder**:

**On Linux/Mac:**
```bash
cd /workspace
zip -r data-monitor-server.zip server/
```

**On Windows:**
- Right-click on the `server` folder
- Select "Send to" > "Compressed (zipped) folder"

3. **The ZIP should contain**:
   - server.js
   - package.json
   - .gitignore (optional)

### Step 3: Upload Files to cPanel

1. **Login to cPanel**

2. **Open File Manager**:
   - Navigate to File Manager
   - Go to your public_html directory (or create a subdirectory)

3. **Create a directory for your app**:
   - Click "New Folder"
   - Name it: `data-monitor` (or any name you prefer)
   - Enter the directory

4. **Upload the ZIP file**:
   - Click "Upload" button
   - Select `data-monitor-server.zip`
   - Wait for upload to complete

5. **Extract the ZIP**:
   - Right-click on the ZIP file
   - Select "Extract"
   - The files should now be in `data-monitor/server/` directory

6. **Move files up one level** (optional but cleaner):
   - Move all files from `data-monitor/server/` to `data-monitor/`
   - Delete the empty `server` folder

### Step 4: Set Up Node.js Application in cPanel

1. **Go back to cPanel home**

2. **Click "Setup Node.js App"** (or "Node.js Selector")

3. **Click "Create Application"**

4. **Fill in the form**:

   | Field | Value |
   |-------|-------|
   | Node.js version | 18.x or latest available |
   | Application mode | Production |
   | Application root | `data-monitor` (or your folder name) |
   | Application URL | Your domain or subdomain |
   | Application startup file | `server.js` |
   | Environment variables | (leave empty for now) |

5. **Click "Create"**

### Step 5: Install Dependencies

After creating the app, cPanel will show you a command to run in Terminal.

1. **In cPanel, open "Terminal"** (may be called "Terminal" or "SSH Access")

2. **Navigate to your app directory**:
```bash
cd data-monitor
```

3. **Install Node.js dependencies**:
```bash
npm install
```

Wait for installation to complete. You should see packages being installed.

4. **Verify installation**:
```bash
ls node_modules
```

You should see folders like: `express`, `multer`, `cors`, etc.

### Step 6: Configure the Application

1. **Still in Terminal, edit server.js if needed**:
```bash
nano server.js
```

**Important changes for cPanel:**

Change the port listening from:
```javascript
app.listen(PORT, '0.0.0.0', () => {
```

To:
```javascript
app.listen(PORT, () => {
```

And update the PORT to use what cPanel assigned (cPanel will show you the port).

Or better yet, use environment variable:
```javascript
const PORT = process.env.PORT || 3000;
```

2. **Save and exit**:
   - Press `Ctrl + X`
   - Press `Y` to confirm
   - Press `Enter`

### Step 7: Start the Application

1. **Go back to "Setup Node.js App" in cPanel**

2. **Find your application in the list**

3. **Click on the application name or "Edit"**

4. **Click "Start App"** or the play button

5. **Check status** - it should show "Running"

6. **Note the URL** - cPanel will show you the full URL, like:
   - `http://yourdomain.com` (if you set it up on main domain)
   - `http://yourdomain.com:PORT` (with port number)
   - `http://subdomain.yourdomain.com`

### Step 8: Test the Server

1. **In your browser, visit**:
```
http://yourdomain.com/api/health
```

Or if cPanel assigned a port:
```
http://yourdomain.com:PORT/api/health
```

2. **You should see**:
```json
{
  "status": "ok",
  "message": "Server is running",
  "timestamp": "2025-11-28T..."
}
```

✅ **If you see this, your server is working!**

❌ **If you get an error**, see troubleshooting section below.

---

## Part 2: Configure Android App in Android Studio

### Step 1: Open Project in Android Studio

1. **Launch Android Studio**

2. **Open Project**:
   - File > Open
   - Navigate to `/workspace`
   - Click "OK"

3. **Wait for Gradle sync** to complete (first time may take 5-10 minutes)

### Step 2: Configure Server URL

1. **In Android Studio, open the Project view** (left sidebar)

2. **Navigate to**:
```
app > src > main > java > com > example > datamonitor > network > DataUploader.kt
```

3. **Find this line** (around line 14):
```kotlin
private const val SERVER_URL = "http://YOUR_SERVER_IP:3000"
```

4. **Replace with your cPanel server URL**:

**If no port number:**
```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

**If cPanel assigned a port:**
```kotlin
private const val SERVER_URL = "http://yourdomain.com:PORT"
```

**Example:**
```kotlin
private const val SERVER_URL = "http://monitor.example.com"
```

5. **Save the file** (Ctrl+S or Cmd+S)

### Step 3: Build the App

1. **In Android Studio, click**:
   - Build > Make Project
   - Or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

2. **Wait for build to complete** - check the Build tab at bottom

3. **Fix any errors** if they appear (usually none if you just changed the URL)

### Step 4: Connect Your Android Device

**Option A: Physical Device (Recommended)**

1. **Enable Developer Options on your Android device**:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings
   - Find "Developer Options"

2. **Enable USB Debugging**:
   - In Developer Options
   - Turn on "USB Debugging"

3. **Connect device to computer via USB**

4. **Accept the prompt** on your phone to allow USB debugging

5. **In Android Studio**, you should see your device in the device dropdown (top toolbar)

**Option B: Emulator (Testing Only)**

1. **In Android Studio**:
   - Tools > Device Manager
   - Click "Create Device"
   - Select a device (e.g., Pixel 6)
   - Download and select a system image (API 30+)
   - Click "Finish"

2. **Start the emulator** by clicking the play button

### Step 5: Run the App

1. **Select your device** from the dropdown (top toolbar)

2. **Click the Run button** (green play icon) or press `Shift+F10`

3. **Wait for installation** - Android Studio will:
   - Build the APK
   - Install it on your device
   - Launch the app

4. **The app should open** on your device!

---

## Part 3: Test the Complete Setup

### Step 1: Grant Permissions

1. **When the app opens**, you'll see a privacy warning
   - Click "I Understand"

2. **Click "Start Monitoring"**

3. **Grant permissions** when prompted:
   - ✅ Location (select "Allow all the time" or "While using the app")
   - ✅ Microphone
   - ✅ Files and media
   - ✅ Background location (Android 10+)

**Note**: You may need to grant some permissions manually:
- Go to: Settings > Apps > Data Monitor > Permissions
- Enable all permissions

### Step 2: Start Monitoring

1. **In the app, tap "Start Monitoring"**

2. **You should see notifications**:
   - "Location Tracking Active"
   - "Audio Recording Active"

3. **The app will now**:
   - Track your location every minute
   - Record audio in 1-minute segments
   - Upload everything to your cPanel server

### Step 3: Verify Data is Being Received

**Method 1: Check via Browser**

Visit this URL in your browser:
```
http://yourdomain.com/api/stats
```

You should see something like:
```json
{
  "locations": 5,
  "audioRecordings": 2,
  "totalAudioSizeMB": "15.34",
  "dataDirectory": "/home/username/data-monitor/data"
}
```

**Method 2: Check Recent Locations**

Visit:
```
http://yourdomain.com/api/locations/recent?limit=5
```

You should see your recent location data.

**Method 3: Check cPanel File Manager**

1. Go to cPanel > File Manager
2. Navigate to: `data-monitor/data/`
3. You should see two folders:
   - `locations/` - Contains JSON files with location data
   - `audio/` - Contains PCM files with audio recordings

**Method 4: Check Android Studio Logcat**

1. In Android Studio, open Logcat (bottom toolbar)
2. Filter by: `LocationService` or `DataUploader`
3. Look for messages like:
   - "Location uploaded: 37.7749, -122.4194"
   - "Audio uploaded: audio_20251128_143045.pcm"

---

## Part 4: View Collected Data

### View Statistics

**Browser:**
```
http://yourdomain.com/api/stats
```

### View Recent Locations

**Browser:**
```
http://yourdomain.com/api/locations/recent?limit=10
```

### Download Location Data

1. **Go to cPanel > File Manager**
2. **Navigate to**: `data-monitor/data/locations/`
3. **You'll see files like**:
   - `location_2025-11-28T14-30-45.json` (individual locations)
   - `locations_2025-11-28.jsonl` (daily log file)
4. **Right-click and "Download"** any file to view on your computer

### Download Audio Recordings

1. **Go to cPanel > File Manager**
2. **Navigate to**: `data-monitor/data/audio/`
3. **You'll see files like**:
   - `audio_20251128_143045.pcm` (audio recording)
   - `audio_20251128_143045_metadata.json` (metadata)
4. **Download the PCM files**

**To play PCM audio files:**
- Use Audacity (free): File > Import > Raw Data
  - Encoding: Signed 16-bit PCM
  - Byte order: Little-endian
  - Channels: 1 (Mono)
  - Sample rate: 44100 Hz

---

## Troubleshooting

### Issue 1: "Cannot connect to server"

**Check 1: Is the server running?**
```bash
# In cPanel Terminal
cd data-monitor
pm2 status
# Or check in cPanel Node.js App interface
```

**Check 2: Test server directly**
```bash
curl http://yourdomain.com/api/health
```

**Check 3: Check the URL in Android app**
- Make sure SERVER_URL in DataUploader.kt matches your domain exactly
- Make sure it starts with `http://` (not `https://` unless you set up SSL)

**Check 4: Firewall/Security**
- Some cPanel setups block external connections
- Check with your hosting provider
- You may need to whitelist the port in cPanel firewall

### Issue 2: "App crashes on startup"

**Solution 1: Check Android Studio Logcat**
- Look for error messages in Logcat
- Common issue: Missing dependencies

**Solution 2: Rebuild the project**
```
Build > Clean Project
Build > Rebuild Project
```

**Solution 3: Sync Gradle**
```
File > Sync Project with Gradle Files
```

### Issue 3: "Permissions not granted"

**Solution:**
1. Uninstall the app completely
2. Reinstall it
3. Grant permissions manually:
   - Settings > Apps > Data Monitor > Permissions
   - Enable all permissions

### Issue 4: "Server returns 404"

**Possible causes:**
- Server didn't start properly in cPanel
- Wrong URL in Android app
- Application root path wrong in cPanel

**Solution:**
1. Go to cPanel > Setup Node.js App
2. Make sure the app is "Running"
3. Click "Restart" if needed
4. Check the logs (if cPanel provides log viewer)

### Issue 5: "Location/Audio not uploading"

**Check Android logs:**
1. Open Logcat in Android Studio
2. Filter by: `DataUploader`
3. Look for error messages

**Common issues:**
- No internet connection on device
- Server URL wrong
- Server not accepting requests
- CORS issues (server.js already has CORS enabled)

### Issue 6: cPanel Shows "Application Failed"

**Solution:**
1. Check Node.js version compatibility
2. Make sure all dependencies installed (`npm install`)
3. Check startup file name is correct (`server.js`)
4. View error logs in cPanel (if available)

---

## Alternative: If cPanel Doesn't Support Node.js

If your cPanel hosting doesn't support Node.js, here are alternatives:

### Option 1: Use a Node.js-Friendly Host

**Free options:**
- Railway.app (free tier available)
- Render.com (free tier available)
- Fly.io (free tier available)

**Paid options ($5-10/month):**
- DigitalOcean App Platform
- Heroku
- AWS Elastic Beanstalk

### Option 2: Use a Subdomain with Different Host

- Keep your main site on cPanel
- Use a free Node.js host for the API
- Point a subdomain (api.yourdomain.com) to the Node.js host

### Option 3: Rewrite Server in PHP

If your cPanel only supports PHP, you could rewrite the server in PHP. Let me know if you need this.

---

## Security Recommendations for Production

### 1. Enable HTTPS (SSL/TLS)

**In cPanel:**
1. Go to "SSL/TLS Status"
2. Enable "AutoSSL" or install Let's Encrypt certificate
3. Your domain will get HTTPS

**Update Android app:**
```kotlin
private const val SERVER_URL = "https://yourdomain.com"
```

### 2. Add Password Protection

**In cPanel, protect the directory:**
1. Go to "Directory Privacy"
2. Select `data-monitor` folder
3. Enable password protection
4. Create username and password

**Update Android app** to send credentials with requests.

### 3. Add .htaccess Rules

Create `.htaccess` in your `data-monitor` folder:
```apache
# Block access to data directory
<DirectoryMatch "data">
    Order allow,deny
    Deny from all
</DirectoryMatch>

# Only allow POST requests to API endpoints
<Location "/api">
    RewriteEngine On
    RewriteCond %{REQUEST_METHOD} !^(POST|GET)$
    RewriteRule .* - [F,L]
</Location>
```

---

## Monitoring Your Server

### Check Disk Space

**In cPanel:**
1. Check disk usage on main dashboard
2. Audio files can fill up space quickly!

**Set up email alerts:**
1. Contact your hosting provider
2. Ask them to enable disk space alerts

### Check Application Status

**In cPanel:**
1. Go to "Setup Node.js App"
2. Check if app shows "Running"
3. Restart if needed

### View Logs

**In cPanel Terminal:**
```bash
cd data-monitor
tail -f logs/output.log  # If cPanel creates logs
```

Or use the logs viewer in cPanel Node.js App interface.

---

## Quick Reference

### Your Configuration

Write down your details:

```
Domain/URL: _________________________________

Server full URL: ____________________________

cPanel username: ____________________________

Application directory: ______________________

Node.js version: ____________________________

PORT (if assigned): _________________________
```

### Important URLs

```
Health check:
http://yourdomain.com/api/health

Statistics:
http://yourdomain.com/api/stats

Recent locations:
http://yourdomain.com/api/locations/recent?limit=10
```

### Important Files to Update

**Android app:**
```
File: app/src/main/java/com/example/datamonitor/network/DataUploader.kt
Line: private const val SERVER_URL = "http://yourdomain.com"
```

**Server (if needed):**
```
File: server.js
Line: const PORT = process.env.PORT || 3000;
```

---

## Summary

You should now have:

✅ Node.js server running on cPanel  
✅ Android app built in Android Studio  
✅ App connected to your server  
✅ Location tracking working  
✅ Audio recording working  
✅ Data being saved to cPanel  

**Test it:**
1. Open the app on your Android device
2. Start monitoring
3. Wait 2-3 minutes
4. Check `http://yourdomain.com/api/stats` in browser
5. You should see location and audio counts increasing!

---

## Need Help?

**cPanel Issues:**
- Check cPanel documentation
- Contact your hosting provider support
- Make sure Node.js is enabled on your plan

**Android Studio Issues:**
- Check Logcat for errors
- Rebuild project
- Make sure Gradle sync completed

**Connection Issues:**
- Test server URL in browser first
- Make sure URL is correct in DataUploader.kt
- Check internet connection on device

---

Good luck with your deployment! 🚀
