const express = require('express');
const multer = require('multer');
const cors = require('cors');
const fs = require('fs-extra');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ extended: true, limit: '50mb' }));

// Create directories for storing data
const DATA_DIR = path.join(__dirname, 'data');
const LOCATION_DIR = path.join(DATA_DIR, 'locations');
const AUDIO_DIR = path.join(DATA_DIR, 'audio');

fs.ensureDirSync(LOCATION_DIR);
fs.ensureDirSync(AUDIO_DIR);

// Configure multer for audio file uploads
const audioStorage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, AUDIO_DIR);
    },
    filename: (req, file, cb) => {
        const timestamp = new Date().toISOString().replace(/:/g, '-');
        cb(null, `audio_${timestamp}.pcm`);
    }
});

const upload = multer({ 
    storage: audioStorage,
    limits: {
        fileSize: 100 * 1024 * 1024 // 100MB max file size
    }
});

// ==================== ROUTES ====================

/**
 * Health check endpoint
 */
app.get('/api/health', (req, res) => {
    res.json({ 
        status: 'ok', 
        message: 'Server is running',
        timestamp: new Date().toISOString()
    });
});

/**
 * Receive location data
 */
app.post('/api/location', async (req, res) => {
    try {
        const locationData = req.body;
        
        // Validate data
        if (!locationData.latitude || !locationData.longitude) {
            return res.status(400).json({ 
                error: 'Invalid location data',
                message: 'Latitude and longitude are required'
            });
        }

        // Log to console
        console.log('📍 Location received:', {
            lat: locationData.latitude,
            lng: locationData.longitude,
            accuracy: locationData.accuracy,
            timestamp: locationData.timestamp
        });

        // Save to file
        const timestamp = new Date().toISOString().replace(/:/g, '-');
        const filename = path.join(LOCATION_DIR, `location_${timestamp}.json`);
        await fs.writeJson(filename, locationData, { spaces: 2 });

        // Also append to daily log file
        const date = new Date().toISOString().split('T')[0];
        const logFile = path.join(LOCATION_DIR, `locations_${date}.jsonl`);
        await fs.appendFile(logFile, JSON.stringify(locationData) + '\n');

        res.json({ 
            success: true, 
            message: 'Location data received',
            saved: filename
        });

    } catch (error) {
        console.error('Error saving location:', error);
        res.status(500).json({ 
            error: 'Failed to save location data',
            message: error.message
        });
    }
});

/**
 * Receive audio recording
 */
app.post('/api/audio', upload.single('audio'), async (req, res) => {
    try {
        if (!req.file) {
            return res.status(400).json({ 
                error: 'No audio file provided'
            });
        }

        const timestamp = req.body.timestamp || Date.now();
        
        console.log('🎤 Audio received:', {
            filename: req.file.filename,
            size: req.file.size,
            timestamp: new Date(parseInt(timestamp)).toISOString()
        });

        // Save metadata
        const metadataFile = req.file.path.replace('.pcm', '_metadata.json');
        await fs.writeJson(metadataFile, {
            filename: req.file.filename,
            originalName: req.file.originalname,
            size: req.file.size,
            mimetype: req.file.mimetype,
            timestamp: timestamp,
            uploadedAt: new Date().toISOString()
        }, { spaces: 2 });

        res.json({ 
            success: true, 
            message: 'Audio file received',
            filename: req.file.filename,
            size: req.file.size
        });

    } catch (error) {
        console.error('Error saving audio:', error);
        res.status(500).json({ 
            error: 'Failed to save audio file',
            message: error.message
        });
    }
});

/**
 * Get statistics
 */
app.get('/api/stats', async (req, res) => {
    try {
        const locationFiles = await fs.readdir(LOCATION_DIR);
        const audioFiles = await fs.readdir(AUDIO_DIR);
        
        const locationCount = locationFiles.filter(f => f.endsWith('.json')).length;
        const audioCount = audioFiles.filter(f => f.endsWith('.pcm')).length;

        // Calculate total audio storage
        let totalAudioSize = 0;
        for (const file of audioFiles) {
            if (file.endsWith('.pcm')) {
                const stats = await fs.stat(path.join(AUDIO_DIR, file));
                totalAudioSize += stats.size;
            }
        }

        res.json({
            locations: locationCount,
            audioRecordings: audioCount,
            totalAudioSizeMB: (totalAudioSize / 1024 / 1024).toFixed(2),
            dataDirectory: DATA_DIR
        });

    } catch (error) {
        console.error('Error getting stats:', error);
        res.status(500).json({ 
            error: 'Failed to get statistics',
            message: error.message
        });
    }
});

/**
 * Get recent locations
 */
app.get('/api/locations/recent', async (req, res) => {
    try {
        const limit = parseInt(req.query.limit) || 10;
        const date = new Date().toISOString().split('T')[0];
        const logFile = path.join(LOCATION_DIR, `locations_${date}.jsonl`);

        if (!await fs.pathExists(logFile)) {
            return res.json({ locations: [] });
        }

        const content = await fs.readFile(logFile, 'utf-8');
        const lines = content.trim().split('\n').filter(line => line);
        const locations = lines.slice(-limit).map(line => JSON.parse(line));

        res.json({ 
            locations: locations.reverse(),
            count: locations.length
        });

    } catch (error) {
        console.error('Error getting recent locations:', error);
        res.status(500).json({ 
            error: 'Failed to get recent locations',
            message: error.message
        });
    }
});

// ==================== START SERVER ====================

app.listen(PORT, '0.0.0.0', () => {
    console.log('='.repeat(50));
    console.log('🚀 Data Monitor Server Started');
    console.log('='.repeat(50));
    console.log(`📡 Server running on: http://0.0.0.0:${PORT}`);
    console.log(`📁 Data directory: ${DATA_DIR}`);
    console.log(`📍 Location data: ${LOCATION_DIR}`);
    console.log(`🎤 Audio data: ${AUDIO_DIR}`);
    console.log('='.repeat(50));
    console.log('\n⚠️  PRIVACY WARNING:');
    console.log('This server collects location and audio data.');
    console.log('Ensure you have proper authorization and comply with');
    console.log('all applicable privacy laws and regulations.');
    console.log('='.repeat(50) + '\n');
});

// Error handling
app.use((err, req, res, next) => {
    console.error('Server error:', err);
    res.status(500).json({ 
        error: 'Internal server error',
        message: err.message
    });
});
