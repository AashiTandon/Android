# Data Monitor Server

Backend server for receiving and storing location and audio data from the Android Data Monitor app.

## Features

- 📍 **Location Data Collection** - Receives GPS coordinates with metadata
- 🎤 **Audio File Storage** - Accepts and stores PCM audio recordings
- 📊 **Statistics API** - View collection statistics
- 📝 **Daily Logs** - Organizes data by date
- 🔍 **Query Recent Data** - API to retrieve recent locations

## Quick Start

### Install Dependencies

```bash
npm install
```

### Start Server

```bash
npm start
```

Server will start on `http://0.0.0.0:3000`

### Development Mode (with auto-reload)

```bash
npm run dev
```

## API Endpoints

### Health Check

**GET** `/api/health`

Check if server is running.

**Response:**
```json
{
  "status": "ok",
  "message": "Server is running",
  "timestamp": "2025-11-28T14:30:45.123Z"
}
```

---

### Submit Location

**POST** `/api/location`

Submit location data.

**Request Body:**
```json
{
  "latitude": 37.7749,
  "longitude": -122.4194,
  "accuracy": 15.5,
  "altitude": 10.0,
  "speed": 0.0,
  "timestamp": "2025-11-28 14:30:45"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Location data received",
  "saved": "/path/to/file.json"
}
```

---

### Upload Audio

**POST** `/api/audio`

Upload audio recording.

**Request:** Multipart form data
- `audio` (file) - PCM audio file
- `timestamp` (string) - Unix timestamp in milliseconds

**Response:**
```json
{
  "success": true,
  "message": "Audio file received",
  "filename": "audio_20251128_143045.pcm",
  "size": 5242880
}
```

---

### Get Statistics

**GET** `/api/stats`

Get collection statistics.

**Response:**
```json
{
  "locations": 150,
  "audioRecordings": 25,
  "totalAudioSizeMB": "125.67",
  "dataDirectory": "/path/to/data"
}
```

---

### Get Recent Locations

**GET** `/api/locations/recent?limit=10`

Get recent location data.

**Query Parameters:**
- `limit` (optional, default: 10) - Number of locations to return

**Response:**
```json
{
  "locations": [
    {
      "latitude": 37.7749,
      "longitude": -122.4194,
      "accuracy": 15.5,
      "altitude": 10.0,
      "speed": 0.0,
      "timestamp": "2025-11-28 14:30:45"
    }
  ],
  "count": 1
}
```

## Data Storage

### Directory Structure

```
server/
└── data/
    ├── locations/
    │   ├── location_2025-11-28T14-30-45.json    # Individual location files
    │   ├── locations_2025-11-28.jsonl           # Daily log (JSON Lines)
    │   └── ...
    └── audio/
        ├── audio_20251128_143045.pcm            # Audio recording
        ├── audio_20251128_143045_metadata.json  # Audio metadata
        └── ...
```

### Location Files

Each location is saved as:
1. Individual JSON file for easy access
2. Appended to daily JSONL (JSON Lines) log file

### Audio Files

Audio recordings are saved with:
- Main file: `.pcm` format (raw audio)
- Metadata file: `.json` format with file information

## Configuration

### Port Configuration

Change the port in `server.js`:

```javascript
const PORT = process.env.PORT || 3000;
```

Or use environment variable:

```bash
PORT=8080 npm start
```

### File Size Limits

Modify in `server.js`:

```javascript
const upload = multer({ 
    storage: audioStorage,
    limits: {
        fileSize: 100 * 1024 * 1024 // 100MB
    }
});
```

### CORS Configuration

To restrict access from specific origins:

```javascript
app.use(cors({
    origin: 'http://your-domain.com',
    credentials: true
}));
```

## Production Deployment

### Using PM2 (Recommended)

1. Install PM2:
```bash
npm install -g pm2
```

2. Start server with PM2:
```bash
pm2 start server.js --name "data-monitor"
```

3. Configure auto-restart on system reboot:
```bash
pm2 startup
pm2 save
```

### Using Docker

Create `Dockerfile`:

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --production
COPY . .
EXPOSE 3000
CMD ["node", "server.js"]
```

Build and run:

```bash
docker build -t data-monitor-server .
docker run -d -p 3000:3000 -v $(pwd)/data:/app/data data-monitor-server
```

### Environment Variables

Create `.env` file:

```env
PORT=3000
NODE_ENV=production
DATA_DIR=/var/data/monitor
```

Use with:

```bash
npm install dotenv
```

And in `server.js`:

```javascript
require('dotenv').config();
const PORT = process.env.PORT || 3000;
```

## Security Recommendations

### 1. Add Authentication

```javascript
const apiKey = 'your-secret-api-key';

app.use('/api', (req, res, next) => {
    const key = req.headers['x-api-key'];
    if (key !== apiKey) {
        return res.status(401).json({ error: 'Unauthorized' });
    }
    next();
});
```

### 2. Enable HTTPS

Use a reverse proxy like Nginx with SSL certificate:

```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    location / {
        proxy_pass http://localhost:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 3. Rate Limiting

```bash
npm install express-rate-limit
```

```javascript
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100 // limit each IP to 100 requests per windowMs
});

app.use('/api', limiter);
```

### 4. Input Validation

```bash
npm install express-validator
```

```javascript
const { body, validationResult } = require('express-validator');

app.post('/api/location',
    body('latitude').isFloat({ min: -90, max: 90 }),
    body('longitude').isFloat({ min: -180, max: 180 }),
    (req, res) => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({ errors: errors.array() });
        }
        // Process location...
    }
);
```

## Monitoring

### Server Logs

View logs in real-time:

```bash
# Standard output
npm start

# With PM2
pm2 logs data-monitor
```

### Disk Space Monitoring

Check data directory size:

```bash
du -sh data/
```

Set up automatic cleanup:

```javascript
// In server.js
const cleanupOldFiles = async () => {
    const retentionDays = 30;
    const cutoffDate = Date.now() - (retentionDays * 24 * 60 * 60 * 1000);
    
    // Implementation here
};

// Run daily
setInterval(cleanupOldFiles, 24 * 60 * 60 * 1000);
```

## Troubleshooting

### "Port already in use"

```bash
# Find process using port 3000
lsof -i :3000

# Kill the process
kill -9 <PID>
```

### "EACCES: permission denied"

```bash
# Check data directory permissions
ls -la data/

# Fix permissions
chmod -R 755 data/
```

### "Cannot find module"

```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install
```

## Testing

### Manual Testing

```bash
# Test health endpoint
curl http://localhost:3000/api/health

# Test location submission
curl -X POST http://localhost:3000/api/location \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 37.7749,
    "longitude": -122.4194,
    "accuracy": 15.5,
    "altitude": 10.0,
    "speed": 0.0,
    "timestamp": "2025-11-28 14:30:45"
  }'

# Test audio upload
curl -X POST http://localhost:3000/api/audio \
  -F "audio=@test.pcm" \
  -F "timestamp=1700000000000"

# Get statistics
curl http://localhost:3000/api/stats
```

### Load Testing

Using Apache Bench:

```bash
ab -n 1000 -c 10 http://localhost:3000/api/health
```

## Performance Optimization

### 1. Enable Compression

```bash
npm install compression
```

```javascript
const compression = require('compression');
app.use(compression());
```

### 2. Database Integration

For large-scale deployments, consider using a database:

```javascript
// Example with MongoDB
const mongoose = require('mongoose');

const LocationSchema = new mongoose.Schema({
    latitude: Number,
    longitude: Number,
    accuracy: Number,
    altitude: Number,
    speed: Number,
    timestamp: Date
});

const Location = mongoose.model('Location', LocationSchema);
```

### 3. Caching

```bash
npm install node-cache
```

```javascript
const NodeCache = require('node-cache');
const cache = new NodeCache({ stdTTL: 600 });

app.get('/api/stats', async (req, res) => {
    const cached = cache.get('stats');
    if (cached) return res.json(cached);
    
    const stats = await calculateStats();
    cache.set('stats', stats);
    res.json(stats);
});
```

## Backup

### Automated Backup Script

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/backups/data-monitor"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p "$BACKUP_DIR"
tar -czf "$BACKUP_DIR/backup_$DATE.tar.gz" data/

# Keep only last 7 days
find "$BACKUP_DIR" -name "backup_*.tar.gz" -mtime +7 -delete
```

Schedule with cron:

```bash
# Run daily at 2 AM
0 2 * * * /path/to/backup.sh
```

## License

MIT License - See LICENSE file for details

## Support

For issues or questions about the server:
1. Check server logs
2. Verify network configuration
3. Ensure sufficient disk space
4. Review firewall settings

## Changelog

### Version 1.0.0
- Initial release
- Location data collection
- Audio file upload
- Statistics API
- Recent locations query
