<?php
/**
 * Recent Locations Endpoint
 * GET /api/recent.php?limit=10
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

// Get limit from query parameter
$limit = isset($_GET['limit']) ? intval($_GET['limit']) : 10;

// Get data directory
$dataDir = dirname(__DIR__) . '/data';
$locationDir = $dataDir . '/locations';

// Get today's log file
$date = date('Y-m-d');
$logFile = $locationDir . '/locations_' . $date . '.jsonl';

$locations = [];

if (file_exists($logFile)) {
    // Read file content
    $content = file_get_contents($logFile);
    $lines = array_filter(explode("\n", trim($content)));
    
    // Get last N lines
    $lines = array_slice($lines, -$limit);
    
    // Parse JSON from each line
    foreach ($lines as $line) {
        $location = json_decode($line, true);
        if ($location) {
            $locations[] = $location;
        }
    }
    
    // Reverse to show newest first
    $locations = array_reverse($locations);
}

// Send response
echo json_encode([
    'locations' => $locations,
    'count' => count($locations)
], JSON_PRETTY_PRINT);
?>
