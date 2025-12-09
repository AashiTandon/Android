<?php
/**
 * Statistics Endpoint
 * GET /api/stats.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Get data directories
$dataDir = dirname(__DIR__) . '/data';
$locationDir = $dataDir . '/locations';
$audioDir = $dataDir . '/audio';

// Initialize counters
$locationCount = 0;
$audioCount = 0;
$totalAudioSize = 0;

// Count location files
if (file_exists($locationDir)) {
    $locationFiles = scandir($locationDir);
    foreach ($locationFiles as $file) {
        if (pathinfo($file, PATHINFO_EXTENSION) === 'json') {
            $locationCount++;
        }
    }
}

// Count audio files and calculate total size
if (file_exists($audioDir)) {
    $audioFiles = scandir($audioDir);
    foreach ($audioFiles as $file) {
        if (pathinfo($file, PATHINFO_EXTENSION) === 'pcm') {
            $audioCount++;
            $filepath = $audioDir . '/' . $file;
            if (file_exists($filepath)) {
                $totalAudioSize += filesize($filepath);
            }
        }
    }
}

// Convert bytes to MB
$totalAudioSizeMB = round($totalAudioSize / 1024 / 1024, 2);

// Send response
echo json_encode([
    'locations' => $locationCount,
    'audioRecordings' => $audioCount,
    'totalAudioSizeMB' => number_format($totalAudioSizeMB, 2),
    'dataDirectory' => $dataDir
], JSON_PRETTY_PRINT);
?>
