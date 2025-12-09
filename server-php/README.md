# Data Monitor Server - PHP Version

Pure PHP server for cPanel hosting. No Node.js required!

## Features

- ✅ Works on any cPanel hosting
- ✅ No special requirements - just PHP
- ✅ Same functionality as Node.js version
- ✅ Easy to deploy and maintain

## Requirements

- PHP 7.0 or higher (most cPanel has PHP 7.4+)
- Apache with mod_rewrite (standard on cPanel)
- File write permissions

## Installation (cPanel)

### Quick Upload

1. **Create ZIP file** from `server-php` folder
2. **Upload to cPanel** File Manager
3. **Extract** in desired location (e.g., `public_html/data-monitor`)
4. **Done!** No npm install, no Node.js setup

### File Structure

```
server-php/
├── api/
│   ├── health.php      # Health check endpoint
│   ├── location.php    # Location data receiver
│   ├── audio.php       # Audio file upload
│   ├── stats.php       # Statistics
│   └── recent.php      # Recent locations
├── data/               # Auto-created for data storage
│   ├── locations/
│   └── audio/
├── .htaccess           # Configuration & security
├── index.php           # Welcome page
└── README.md           # This file
```

## API Endpoints

### Health Check
```
GET /api/health.php

Response:
{
  "status": "ok",
  "message": "Server is running",
  "timestamp": "2025-11-28T14:30:45.123Z",
  "server": "PHP 7.4.0"
}
```

### Submit Location
```
POST /api/location.php
Content-Type: application/json

Body:
{
  "latitude": 37.7749,
  "longitude": -122.4194,
  "accuracy": 15.5,
  "altitude": 10.0,
  "speed": 0.0,
  "timestamp": "2025-11-28 14:30:45"
}

Response:
{
  "success": true,
  "message": "Location data received",
  "saved": "/path/to/file.json"
}
```

### Upload Audio
```
POST /api/audio.php
Content-Type: multipart/form-data

Fields:
- audio (file): PCM audio file
- timestamp (string): Unix timestamp

Response:
{
  "success": true,
  "message": "Audio file received",
  "filename": "audio_20251128_143045.pcm",
  "size": 5242880
}
```

### Get Statistics
```
GET /api/stats.php

Response:
{
  "locations": 150,
  "audioRecordings": 25,
  "totalAudioSizeMB": "125.67",
  "dataDirectory": "/home/user/public_html/data-monitor/data"
}
```

### Get Recent Locations
```
GET /api/recent.php?limit=10

Response:
{
  "locations": [
    {
      "latitude": 37.7749,
      "longitude": -122.4194,
      ...
    }
  ],
  "count": 10
}
```

## Configuration

### Upload Limits

Edit `.htaccess` to change limits:

```apache
php_value upload_max_filesize 100M
php_value post_max_size 100M
php_value max_execution_time 300
```

### File Permissions

If you get "Permission denied" errors:

```bash
chmod 755 server-php/
chmod 755 server-php/api/
chmod 777 server-php/data/  # Or 755 if owned by web user
```

## Testing

### Test Health Endpoint

Visit in browser:
```
http://yourdomain.com/data-monitor/api/health.php
```

### Test Location Submission

```bash
curl -X POST http://yourdomain.com/data-monitor/api/location.php \
  -H "Content-Type: application/json" \
  -d '{"latitude":37.7749,"longitude":-122.4194,"accuracy":15.5,"altitude":10.0,"speed":0.0,"timestamp":"2025-11-28 14:30:45"}'
```

### Test Statistics

Visit in browser:
```
http://yourdomain.com/data-monitor/api/stats.php
```

## Security

### Protect Data Directory

The included `.htaccess` already blocks direct access to the `data/` folder.

### Add Authentication (Optional)

Add to each API file:

```php
// Simple API key authentication
$apiKey = 'your-secret-key-here';
$providedKey = $_SERVER['HTTP_X_API_KEY'] ?? '';

if ($providedKey !== $apiKey) {
    http_response_code(401);
    echo json_encode(['error' => 'Unauthorized']);
    exit();
}
```

Then in Android app:
```kotlin
val request = Request.Builder()
    .url("$SERVER_URL/api/location.php")
    .addHeader("X-API-Key", "your-secret-key-here")
    .post(body)
    .build()
```

### Enable HTTPS

In cPanel:
1. Go to "SSL/TLS Status"
2. Enable AutoSSL
3. Your site gets HTTPS automatically

## Troubleshooting

### "Permission denied" writing files

```bash
chmod -R 755 server-php/
chmod -R 777 server-php/data/
```

Or in cPanel File Manager:
- Right-click folder → Change Permissions → 755 or 777

### "Upload file too large"

Edit `.htaccess`:
```apache
php_value upload_max_filesize 200M
php_value post_max_size 200M
```

Or ask hosting provider to increase limits.

### "Internal Server Error"

Check cPanel Error Logs:
1. cPanel → Metrics → Errors
2. Look for PHP errors

Common causes:
- Syntax error in PHP file
- Wrong file permissions
- PHP version too old

### Can't access endpoints

Check:
1. Files uploaded correctly
2. `.htaccess` file present
3. mod_rewrite enabled (ask hosting)
4. Correct URL (include `/api/` in path)

## Advantages over Node.js

✅ **No special setup** - Works immediately  
✅ **No npm install** - No dependencies  
✅ **No processes to manage** - Apache handles it  
✅ **Better cPanel integration** - Native PHP support  
✅ **Lower resource usage** - No Node.js daemon  
✅ **Easier debugging** - Error logs in cPanel  

## Logging

Errors are logged to cPanel error log automatically.

To view:
1. cPanel → Metrics → Errors
2. Or check: `/home/username/public_html/error_log`

## Performance

PHP handles:
- Hundreds of requests per minute
- Large file uploads (100MB+)
- Concurrent requests

For very high traffic (1000+ req/min), consider:
- PHP-FPM (ask hosting provider)
- Redis/Memcached caching
- CDN for static assets

## Data Storage

Same format as Node.js version:

**Locations:**
- Individual JSON files
- Daily JSONL log files

**Audio:**
- PCM format files
- JSON metadata files

## Backup

Backup the entire `data/` folder regularly:

```bash
# Via SSH
tar -czf backup.tar.gz data/

# Or use cPanel Backup tool
```

## Migration from Node.js

If you have existing Node.js server data:

1. Copy `data/` folder from Node.js server
2. Place in PHP server root
3. Data structure is identical - works immediately!

## License

MIT License - See main LICENSE file

## Support

- Check cPanel error logs
- Verify file permissions
- Test endpoints with curl
- Check PHP version (7.0+ required)
