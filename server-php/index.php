<?php
/**
 * Data Monitor Server - PHP Version
 * Main entry point
 */
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Data Monitor Server</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .container {
            background: white;
            border-radius: 20px;
            padding: 40px;
            max-width: 600px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 32px;
        }
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-size: 14px;
        }
        .status {
            background: #d4edda;
            color: #155724;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 30px;
            border-left: 4px solid #28a745;
        }
        .endpoints {
            margin-top: 20px;
        }
        .endpoint {
            background: #f8f9fa;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 8px;
            border-left: 3px solid #667eea;
        }
        .method {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: bold;
            margin-right: 10px;
        }
        .get { background: #28a745; color: white; }
        .post { background: #007bff; color: white; }
        .url {
            font-family: 'Courier New', monospace;
            color: #495057;
        }
        .warning {
            background: #fff3cd;
            color: #856404;
            padding: 15px;
            border-radius: 10px;
            margin-top: 20px;
            border-left: 4px solid #ffc107;
        }
        a {
            color: #667eea;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 Data Monitor Server</h1>
        <p class="subtitle">PHP Version - Running on cPanel</p>
        
        <div class="status">
            ✅ <strong>Server Status:</strong> Running<br>
            📅 <strong>Time:</strong> <?php echo date('Y-m-d H:i:s'); ?><br>
            🐘 <strong>PHP:</strong> <?php echo phpversion(); ?>
        </div>

        <h2 style="margin-bottom: 15px;">API Endpoints</h2>
        
        <div class="endpoints">
            <div class="endpoint">
                <span class="method get">GET</span>
                <span class="url"><a href="api/health.php" target="_blank">/api/health.php</a></span>
                <div style="margin-top: 5px; font-size: 13px; color: #666;">Health check endpoint</div>
            </div>

            <div class="endpoint">
                <span class="method post">POST</span>
                <span class="url">/api/location.php</span>
                <div style="margin-top: 5px; font-size: 13px; color: #666;">Submit location data</div>
            </div>

            <div class="endpoint">
                <span class="method post">POST</span>
                <span class="url">/api/audio.php</span>
                <div style="margin-top: 5px; font-size: 13px; color: #666;">Upload audio recording</div>
            </div>

            <div class="endpoint">
                <span class="method get">GET</span>
                <span class="url"><a href="api/stats.php" target="_blank">/api/stats.php</a></span>
                <div style="margin-top: 5px; font-size: 13px; color: #666;">Get statistics</div>
            </div>

            <div class="endpoint">
                <span class="method get">GET</span>
                <span class="url"><a href="api/recent.php?limit=10" target="_blank">/api/recent.php?limit=10</a></span>
                <div style="margin-top: 5px; font-size: 13px; color: #666;">Get recent locations</div>
            </div>
        </div>

        <div class="warning">
            ⚠️ <strong>Privacy Warning:</strong> This server collects location and audio data. 
            Ensure you have proper authorization and comply with all applicable laws.
        </div>

        <div style="margin-top: 20px; text-align: center; color: #999; font-size: 12px;">
            Data Monitor Server v1.0 | PHP Edition
        </div>
    </div>
</body>
</html>
