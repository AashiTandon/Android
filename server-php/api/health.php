<?php
/**
 * Health Check Endpoint
 * GET /api/health.php
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

$response = [
    'status' => 'ok',
    'message' => 'Server is running',
    'timestamp' => date('Y-m-d\TH:i:s.v\Z'),
    'server' => 'PHP ' . phpversion()
];

echo json_encode($response, JSON_PRETTY_PRINT);
?>
