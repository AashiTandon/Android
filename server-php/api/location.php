<?php
/**
 * Location Data Endpoint
 * POST /api/location.php
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

// Get JSON data from request body
$json = file_get_contents('php://input');
$locationData = json_decode($json, true);

// Validate data
if (!$locationData || !isset($locationData['latitude']) || !isset($locationData['longitude'])) {
    http_response_code(400);
    echo json_encode([
        'error' => 'Invalid location data',
        'message' => 'Latitude and longitude are required'
    ]);
    exit();
}

// Create data directories if they don't exist
$dataDir = dirname(__DIR__) . '/data';
$locationDir = $dataDir . '/locations';

if (!file_exists($dataDir)) {
    mkdir($dataDir, 0755, true);
}
if (!file_exists($locationDir)) {
    mkdir($locationDir, 0755, true);
}

// Save individual JSON file
$timestamp = date('Y-m-d\TH-i-s');
$filename = $locationDir . '/location_' . $timestamp . '.json';
file_put_contents($filename, json_encode($locationData, JSON_PRETTY_PRINT));

// Append to daily log file (JSONL format)
$date = date('Y-m-d');
$logFile = $locationDir . '/locations_' . $date . '.jsonl';
file_put_contents($logFile, json_encode($locationData) . "\n", FILE_APPEND);

// Log to console (appears in cPanel error logs)
error_log("📍 Location received: lat={$locationData['latitude']}, lng={$locationData['longitude']}");

// Send response
echo json_encode([
    'success' => true,
    'message' => 'Location data received',
    'saved' => $filename
], JSON_PRETTY_PRINT);
?>
