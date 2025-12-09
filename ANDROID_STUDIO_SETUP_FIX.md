# Android Studio Setup - Error Fix Guide

If you're seeing errors in AndroidManifest.xml, follow these steps:

## ✅ I've Fixed the Project Structure!

The errors were due to incorrect file locations. I've fixed:

1. ✅ Moved `AndroidManifest.xml` to correct location
2. ✅ Created missing resource files (`colors.xml`, `themes.xml`)
3. ✅ Updated build configuration files
4. ✅ Fixed icon references
5. ✅ Created proper Gradle files

## 🔄 What You Need to Do Now

### Step 1: Close and Reopen the Project

1. **In Android Studio**: File → Close Project
2. **Reopen**: File → Open → Select `/workspace`
3. **Wait** for Gradle sync to complete (this may take 5-10 minutes)

### Step 2: Sync Gradle Files

If errors persist:
1. **File → Sync Project with Gradle Files**
2. Wait for sync to complete
3. Check the "Build" tab at bottom for any messages

### Step 3: Clean and Rebuild

1. **Build → Clean Project**
2. Wait for completion
3. **Build → Rebuild Project**
4. Wait for build to finish

### Step 4: Invalidate Caches (if still issues)

1. **File → Invalidate Caches / Restart**
2. Select "Invalidate and Restart"
3. Wait for Android Studio to restart
4. Let it re-index the project

---

## 📁 Correct Project Structure Now

```
/workspace/
├── build.gradle.kts              ✅ Project-level build file
├── settings.gradle.kts           ✅ Settings file
├── gradle.properties             ✅ Gradle properties
├── gradlew                       ✅ Gradle wrapper
│
└── app/
    ├── build.gradle.kts          ✅ App-level build file
    ├── proguard-rules.pro        ✅ ProGuard rules
    │
    └── src/
        └── main/
            ├── AndroidManifest.xml         ✅ MOVED HERE!
            │
            ├── java/
            │   └── com/example/datamonitor/
            │       ├── MainActivity.kt
            │       ├── data/
            │       │   └── LocationData.kt
            │       ├── network/
            │       │   └── DataUploader.kt
            │       └── services/
            │           ├── LocationService.kt
            │           └── AudioRecordingService.kt
            │
            └── res/
                ├── layout/
                │   └── activity_main.xml
                ├── values/
                │   ├── strings.xml     ✅ Existing
                │   ├── colors.xml      ✅ CREATED
                │   └── themes.xml      ✅ CREATED
                └── drawable/           ✅ CREATED
```

---

## 🔍 Check for Errors

After syncing, check these:

### 1. AndroidManifest.xml Should Show No Errors

Open: `app/src/main/AndroidManifest.xml`

Should show green checkmark ✅ (no red underlines)

### 2. Build Should Succeed

Bottom panel should show: **BUILD SUCCESSFUL**

### 3. Gradle Sync Should Complete

Look for: **Gradle sync finished** in bottom status bar

---

## ⚠️ Common Issues & Fixes

### Issue: "SDK location not found"

**Fix:**
1. File → Project Structure
2. SDK Location tab
3. Set Android SDK location (usually auto-detected)
4. Click OK

### Issue: "Gradle version incompatible"

**Fix:**
1. File → Settings → Build, Execution, Deployment → Build Tools → Gradle
2. Use Gradle wrapper (recommended)
3. Click OK

### Issue: "Unable to resolve dependency"

**Check Internet Connection:**
- Gradle needs to download dependencies
- Make sure you're connected to internet
- Wait for download to complete

### Issue: Still showing errors in AndroidManifest.xml

**Check these lines:**

Line ~13: Should reference existing services
```xml
<service android:name=".services.LocationService" ... />
<service android:name=".services.AudioRecordingService" ... />
```

If underlined in red:
1. Make sure the Kotlin files exist:
   - `app/src/main/java/com/example/datamonitor/services/LocationService.kt`
   - `app/src/main/java/com/example/datamonitor/services/AudioRecordingService.kt`
2. Sync Gradle again

### Issue: "Cannot resolve symbol 'R'"

**Fix:**
1. Build → Clean Project
2. Build → Rebuild Project
3. Wait for rebuild to complete

---

## 🧪 Test Your Setup

### 1. Check MainActivity Opens

Click on: `app/src/main/java/com/example/datamonitor/MainActivity.kt`

Should open without errors

### 2. Check DataUploader

Click on: `app/src/main/java/com/example/datamonitor/network/DataUploader.kt`

Verify line 18 has your domain:
```kotlin
private const val SERVER_URL = "http://yourdomain.com"
```

### 3. Try Building

Click: Build → Make Project (Ctrl+F9)

Should complete with "BUILD SUCCESSFUL"

---

## ✅ Once Errors Are Fixed

You can proceed with:

1. **Connect your Android device** (USB debugging enabled)
2. **Click Run button** (green play icon)
3. **Select your device**
4. **Wait for installation**
5. **Grant permissions** on device

---

## 📞 Still Have Issues?

### Check Android Studio Version

Minimum required: **Android Studio Flamingo (2022.2.1)** or newer

Update if needed:
- Help → Check for Updates

### Check Java Version

Required: **JDK 17**

Check in Android Studio:
- File → Project Structure → SDK Location
- Should show JDK 17 or higher

### Post-Sync Errors

If you still see errors after following all steps:

1. **Take a screenshot** of the error message
2. **Check the "Build" tab** at bottom for specific errors
3. **Common error patterns:**
   - Red text = Error
   - Yellow text = Warning (can be ignored)
   - Look for lines starting with "error:"

---

## 🎯 Expected Result

After fixing:

✅ No errors in AndroidManifest.xml  
✅ No errors in Kotlin files  
✅ Gradle sync successful  
✅ Build successful  
✅ Ready to run on device  

---

## 📱 Next Steps After Error Fix

Once Android Studio shows no errors:

1. ✅ Verify SERVER_URL in DataUploader.kt
2. ✅ Connect Android device
3. ✅ Click Run
4. ✅ Install app
5. ✅ Grant permissions
6. ✅ Test monitoring

---

## 🆘 Emergency Reset

If nothing works, try this complete reset:

1. **Close Android Studio**
2. **Delete these folders**:
   - `/workspace/.idea/`
   - `/workspace/.gradle/`
   - `/workspace/app/build/`
3. **Reopen project** in Android Studio
4. **Wait for complete re-indexing** (may take 10-15 minutes)
5. **Sync Gradle**

---

## ✨ Success Indicators

You'll know it's fixed when:

1. ✅ AndroidManifest.xml has no red underlines
2. ✅ All Kotlin files open without errors
3. ✅ Gradle sync shows "✓ Sync successful"
4. ✅ Build tab shows "BUILD SUCCESSFUL"
5. ✅ Device dropdown shows your connected device
6. ✅ Run button is clickable (not grayed out)

---

**Once fixed, continue with: PHP_QUICK_START.md**
