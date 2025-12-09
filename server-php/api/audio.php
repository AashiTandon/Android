<?php
/**
 * Audio Upload Endpoint
 * POST /api/audio.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

// Handle preflight OPTIONS request
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Only accept POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
    exit();
}

// Check if file was uploaded
if (!isset($_FILES['audio']) || $_FILES['audio']['error'] !== UPLOAD_ERR_OK) {
    http_response_code(400);
    echo json_encode(['error' => 'No audio file provided']);
    exit();
}

// Create data directories if they don't exist
$dataDir = dirname(__DIR__) . '/data';
$audioDir = $dataDir . '/audio';

if (!file_exists($dataDir)) {
    mkdir($dataDir, 0755, true);
}
if (!file_exists($audioDir)) {
    mkdir($audioDir, 0755, true);
}

// Generate filename
$timestamp = isset($_POST['timestamp']) ? $_POST['timestamp'] : time() * 1000;
$dateTime = date('Ymd_His');
$filename = 'audio_' . $dateTime . '.pcm';
$filepath = $audioDir . '/' . $filename;

// Move uploaded file
if (!move_uploaded_file($_FILES['audio']['tmp_name'], $filepath)) {
    http_response_code(500);
    echo json_encode(['error' => 'Failed to save audio file']);
    exit();
}

// Get file size
$filesize = filesize($filepath);

// Save metadata
$metadata = [
    'filename' => $filename,
    'originalName' => $_FILES['audio']['name'],
    'size' => $filesize,
    'mimetype' => $_FILES['audio']['type'],
    'timestamp' => $timestamp,
    'uploadedAt' => date('Y-m-d\TH:i:s.v\Z')
];

$metadataFile = $audioDir . '/audio_' . $dateTime . '_metadata.json';
file_put_contents($metadataFile, json_encode($metadata, JSON_PRETTY_PRINT));

// Log to console
error_log("🎤 Audio received: {$filename}, size={$filesize}");

// Send response
echo json_encode([
    'success' => true,
    'message' => 'Audio file received',
    'filename' => $filename,
    'size' => $filesize
], JSON_PRETTY_PRINT);
?>
