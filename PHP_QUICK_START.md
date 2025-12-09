# ⚡ Quick Start: PHP Version (cPanel)

**No Node.js required! Just upload and go!**

---

## 🎯 Summary (3 Steps)

### **STEP 1: Upload PHP Files** (5 min)

1. **Create ZIP** of `/workspace/server-php/` folder
2. **Upload to cPanel** File Manager → `public_html/`
3. **Extract** and organize files
4. **Test**: Visit `http://yourdomain.com/api/health.php`

### **STEP 2: Configure Android App** (5 min)

Edit: `app/src/main/java/com/example/datamonitor/network/DataUploader.kt`

```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

Already configured for PHP! Just update YOUR domain.

### **STEP 3: Run App** (5 min)

1. Open `/workspace` in Android Studio
2. Build project
3. Run on device
4. Grant permissions

---

## 📝 Detailed Steps

### 1. Upload Server (cPanel)

```bash
# Create ZIP
cd /workspace
zip -r data-monitor.zip server-php/
```

Or right-click `server-php` folder → Compress

**Upload to cPanel:**
- cPanel → File Manager → `public_html/`
- Upload ZIP
- Extract
- Move files out of `server-php` subfolder
- Set permissions to 755

**Test:**
```
http://yourdomain.com/api/health.php
```

Should return JSON with "status": "ok"

### 2. Configure Android

**Edit ONE file:**
`app/src/main/java/com/example/datamonitor/network/DataUploader.kt`

**Line 18:**
```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

Change `yourdomain.com` to YOUR actual domain.

**Already done for you:**
- Endpoints already have `.php` extension
- No other changes needed!

### 3. Build & Run

**Android Studio:**
```
File → Open → /workspace
Build → Make Project
Run (green play button)
```

**On phone:**
- Grant all permissions
- Tap "Start Monitoring"

### 4. Verify

**Browser:**
```
http://yourdomain.com/api/stats.php
```

Should show increasing location and audio counts!

---

## 🆚 PHP vs Node.js

| Feature | PHP | Node.js |
|---------|-----|---------|
| **Setup** | Just upload | Need Node.js support |
| **Dependencies** | None | npm install required |
| **cPanel** | Always works | May not be supported |
| **Difficulty** | ⭐ Easy | ⭐⭐ Medium |
| **Time to deploy** | 5 min | 15 min |

**Recommendation: Use PHP for cPanel!**

---

## 📁 File Structure

```
public_html/
├── api/
│   ├── health.php      # Test: yourdomain.com/api/health.php
│   ├── location.php    # Receives GPS data
│   ├── audio.php       # Receives audio files
│   ├── stats.php       # Statistics
│   └── recent.php      # Recent locations
├── data/               # Auto-created
│   ├── locations/      # JSON files
│   └── audio/          # PCM files
├── .htaccess           # Config
└── index.php           # Welcome page
```

---

## 🔧 Configuration Needed

**Only ONE place:**

File: `app/src/main/java/com/example/datamonitor/network/DataUploader.kt`

Line 18:
```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

**Examples:**

```kotlin
// If files in root
private const val SERVER_URL = "http://mysite.com"

// If in subfolder
private const val SERVER_URL = "http://mysite.com/data-monitor"

// With HTTPS (recommended)
private const val SERVER_URL = "https://mysite.com"
```

---

## ✅ Success Checklist

- [ ] `health.php` works in browser
- [ ] Android app built successfully
- [ ] App installed on device
- [ ] Permissions granted
- [ ] Notifications showing
- [ ] `stats.php` shows data
- [ ] Files in cPanel `data/` folder

---

## 🔥 Common Issues

### "404 Not Found"

**Fix:** Check URL includes `/api/health.php` not just `/api/health`

### "500 Internal Error"

**Fix:** Set folder permissions to 755 in cPanel

### "Permission denied"

**Fix:** Set `data/` folder permissions to 777 in cPanel

### "App won't connect"

**Check:**
1. SERVER_URL is correct
2. Endpoints have `.php` already (should be there)
3. Server works in browser first

---

## 📊 URLs to Test

```
Health: http://yourdomain.com/api/health.php
Stats:  http://yourdomain.com/api/stats.php
Recent: http://yourdomain.com/api/recent.php?limit=10
Home:   http://yourdomain.com/
```

---

## 🎉 That's It!

**Total time: 15-20 minutes**

Much easier than Node.js version!

See `CPANEL_PHP_GUIDE.md` for detailed guide.

---

## ⚠️ Legal Warning

Always obtain consent. Use legally. See `PRIVACY_WARNING.md`.

---

**Ready? Start with Step 1!** 🚀
