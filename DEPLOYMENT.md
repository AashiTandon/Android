# Deployment Guide

Complete guide for deploying the Data Monitor system in various environments.

## Overview

This guide covers:
1. Local network deployment (testing)
2. Cloud server deployment (production)
3. Security hardening
4. Monitoring and maintenance

---

## Deployment Scenario 1: Local Network (Testing)

**Use Case:** Testing on local WiFi network

### Requirements
- Computer/server on local network
- Android device on same network
- Node.js installed

### Steps

1. **Set up server on local machine**:
```bash
cd server
npm install
npm start
```

2. **Find local IP address**:
```bash
# Linux/Mac
ip addr show | grep "inet " | grep -v 127.0.0.1

# Windows
ipconfig | findstr IPv4
```

3. **Configure Android app**:
- Edit `DataUploader.kt`
- Set `SERVER_URL = "http://192.168.1.X:3000"` (your local IP)

4. **Build and install app**
5. **Test connection**

**Pros:**
- Easy to set up
- No internet required
- Fast development

**Cons:**
- Only works on local network
- Not accessible from outside
- Server must remain running

---

## Deployment Scenario 2: Cloud Server (Production)

**Use Case:** Remote monitoring accessible from anywhere

### Option A: AWS EC2

#### Setup Steps

1. **Launch EC2 Instance**:
   - Ubuntu Server 22.04 LTS
   - t2.micro (free tier) or larger
   - Security group: Allow TCP port 3000

2. **Connect to instance**:
```bash
ssh -i your-key.pem ubuntu@your-ec2-ip
```

3. **Install Node.js**:
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs
```

4. **Upload server code**:
```bash
# On local machine
scp -i your-key.pem -r server ubuntu@your-ec2-ip:~/
```

5. **Install dependencies and start**:
```bash
cd server
npm install
npm start
```

6. **Use PM2 for persistence**:
```bash
sudo npm install -g pm2
pm2 start server.js --name data-monitor
pm2 startup
pm2 save
```

7. **Configure Android app**:
- Set `SERVER_URL = "http://YOUR_EC2_PUBLIC_IP:3000"`

#### Cost Estimate
- EC2 t2.micro: $0-8/month (free tier eligible)
- Network: ~$0.09/GB outbound
- Storage: $0.10/GB-month

### Option B: DigitalOcean Droplet

1. **Create Droplet**:
   - Ubuntu 22.04
   - Basic plan ($6/month)
   - Add SSH key

2. **Follow similar steps as AWS EC2**

3. **Configure firewall**:
```bash
sudo ufw allow 3000/tcp
sudo ufw enable
```

### Option C: Heroku

1. **Create `Procfile`**:
```
web: node server.js
```

2. **Deploy**:
```bash
cd server
heroku create your-app-name
git init
git add .
git commit -m "Initial commit"
git push heroku main
```

3. **Configure Android app**:
- Set `SERVER_URL = "https://your-app-name.herokuapp.com"`

**Note:** Heroku no longer has free tier as of 2022.

### Option D: Railway.app

1. **Connect GitHub repository**
2. **Deploy with one click**
3. **Get public URL**
4. **Configure Android app with Railway URL**

**Cost:** $5/month + usage

---

## Security Hardening

### 1. Enable HTTPS with Let's Encrypt

**Install Certbot**:
```bash
sudo apt-get update
sudo apt-get install certbot
```

**Get certificate** (requires domain name):
```bash
sudo certbot certonly --standalone -d your-domain.com
```

**Update server for HTTPS**:
```javascript
const https = require('https');
const fs = require('fs');

const options = {
    key: fs.readFileSync('/etc/letsencrypt/live/your-domain.com/privkey.pem'),
    cert: fs.readFileSync('/etc/letsencrypt/live/your-domain.com/fullchain.pem')
};

https.createServer(options, app).listen(443, () => {
    console.log('HTTPS Server running on port 443');
});
```

### 2. Add API Authentication

**Generate API key**:
```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

**Server-side** (`server.js`):
```javascript
const API_KEY = process.env.API_KEY || 'your-generated-key';

app.use('/api', (req, res, next) => {
    const providedKey = req.headers['x-api-key'];
    if (providedKey !== API_KEY) {
        return res.status(401).json({ error: 'Unauthorized' });
    }
    next();
});
```

**Android app** (`DataUploader.kt`):
```kotlin
private const val API_KEY = "your-generated-key"

val request = Request.Builder()
    .url("$SERVER_URL/api/location")
    .addHeader("X-API-Key", API_KEY)
    .post(body)
    .build()
```

### 3. Set Up Firewall

```bash
# Allow SSH
sudo ufw allow 22/tcp

# Allow HTTP/HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Allow custom port (if not using 80/443)
sudo ufw allow 3000/tcp

# Enable firewall
sudo ufw enable
```

### 4. Implement Rate Limiting

```bash
npm install express-rate-limit
```

```javascript
const rateLimit = require('express-rate-limit');

const limiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100, // limit each IP to 100 requests per window
    message: 'Too many requests from this IP'
});

app.use('/api', limiter);
```

### 5. Add Request Logging

```bash
npm install morgan
```

```javascript
const morgan = require('morgan');
app.use(morgan('combined'));
```

---

## Using Reverse Proxy (Nginx)

### Why Use Nginx?
- Handle HTTPS/SSL
- Load balancing
- Static file serving
- Better performance

### Setup

1. **Install Nginx**:
```bash
sudo apt-get install nginx
```

2. **Configure** (`/etc/nginx/sites-available/data-monitor`):
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

3. **Enable site**:
```bash
sudo ln -s /etc/nginx/sites-available/data-monitor /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

4. **Update Android app**:
```kotlin
private const val SERVER_URL = "https://your-domain.com"
```

---

## Database Integration (Optional)

For large-scale deployments, use a database instead of files.

### PostgreSQL Setup

1. **Install PostgreSQL**:
```bash
sudo apt-get install postgresql postgresql-contrib
```

2. **Install pg module**:
```bash
npm install pg
```

3. **Create database**:
```sql
CREATE DATABASE data_monitor;
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    accuracy REAL,
    altitude DOUBLE PRECISION,
    speed REAL,
    timestamp TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

4. **Update server.js**:
```javascript
const { Pool } = require('pg');

const pool = new Pool({
    user: 'your_user',
    host: 'localhost',
    database: 'data_monitor',
    password: 'your_password',
    port: 5432,
});

app.post('/api/location', async (req, res) => {
    const { latitude, longitude, accuracy, altitude, speed, timestamp } = req.body;
    
    try {
        await pool.query(
            'INSERT INTO locations (latitude, longitude, accuracy, altitude, speed, timestamp) VALUES ($1, $2, $3, $4, $5, $6)',
            [latitude, longitude, accuracy, altitude, speed, timestamp]
        );
        res.json({ success: true });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});
```

---

## Monitoring & Maintenance

### 1. Server Monitoring with PM2

```bash
# View logs
pm2 logs data-monitor

# Monitor resources
pm2 monit

# View status
pm2 status
```

### 2. Disk Space Monitoring

```bash
# Check disk usage
df -h

# Check data directory size
du -sh /path/to/server/data
```

**Set up alert**:
```bash
# Add to crontab
0 * * * * /path/to/check_disk.sh
```

`check_disk.sh`:
```bash
#!/bin/bash
USAGE=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
if [ $USAGE -gt 80 ]; then
    echo "Disk usage is ${USAGE}%" | mail -s "Disk Alert" your@email.com
fi
```

### 3. Automated Backups

```bash
#!/bin/bash
# backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups"
DATA_DIR="/home/ubuntu/server/data"

mkdir -p "$BACKUP_DIR"

# Create backup
tar -czf "$BACKUP_DIR/backup_$DATE.tar.gz" "$DATA_DIR"

# Upload to S3 (optional)
aws s3 cp "$BACKUP_DIR/backup_$DATE.tar.gz" s3://your-bucket/backups/

# Keep only last 7 days locally
find "$BACKUP_DIR" -name "backup_*.tar.gz" -mtime +7 -delete
```

**Schedule with cron**:
```bash
crontab -e
# Add: 0 2 * * * /path/to/backup.sh
```

### 4. Log Rotation

Create `/etc/logrotate.d/data-monitor`:
```
/home/ubuntu/server/logs/*.log {
    daily
    rotate 7
    compress
    delaycompress
    notifempty
    create 0644 ubuntu ubuntu
    sharedscripts
    postrotate
        pm2 reloadLogs
    endscript
}
```

---

## Docker Deployment

### Create Dockerfile

```dockerfile
FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --production

# Copy source code
COPY . .

# Create data directory
RUN mkdir -p data/locations data/audio

# Expose port
EXPOSE 3000

# Start server
CMD ["node", "server.js"]
```

### Create docker-compose.yml

```yaml
version: '3.8'

services:
  server:
    build: ./server
    ports:
      - "3000:3000"
    volumes:
      - ./data:/app/data
    environment:
      - NODE_ENV=production
      - PORT=3000
    restart: unless-stopped
```

### Deploy

```bash
docker-compose up -d
```

---

## Troubleshooting

### Server Not Accessible

1. Check server is running: `pm2 status`
2. Check port is open: `netstat -tulpn | grep 3000`
3. Check firewall: `sudo ufw status`
4. Check security group (AWS)
5. Test locally: `curl http://localhost:3000/api/health`

### High Memory Usage

1. Monitor: `pm2 monit`
2. Restart: `pm2 restart data-monitor`
3. Investigate logs: `pm2 logs`
4. Consider adding more RAM or optimize code

### SSL Certificate Issues

1. Verify certificate: `sudo certbot certificates`
2. Renew if needed: `sudo certbot renew`
3. Check Nginx config: `sudo nginx -t`
4. Restart Nginx: `sudo systemctl restart nginx`

---

## Scaling Considerations

### For High Traffic

1. **Use load balancer** (AWS ELB, Nginx)
2. **Multiple server instances** behind load balancer
3. **CDN for static assets** (CloudFlare)
4. **Database connection pooling**
5. **Redis for caching**
6. **Message queue** for async processing (RabbitMQ, AWS SQS)

### For Large Storage

1. **Use object storage** (AWS S3, DigitalOcean Spaces)
2. **Implement data retention policies**
3. **Compress audio files**
4. **Use database for metadata**

---

## Cost Optimization

1. **Use spot instances** (AWS) for non-critical workloads
2. **Implement auto-scaling** for variable load
3. **Use reserved instances** for predictable workloads
4. **Optimize storage** with lifecycle policies
5. **Monitor and alert** on unusual usage

---

## Compliance Checklist

- [ ] HTTPS enabled
- [ ] Authentication implemented
- [ ] Rate limiting configured
- [ ] Logging enabled
- [ ] Backup strategy in place
- [ ] Data retention policy defined
- [ ] Privacy policy created
- [ ] User consent mechanism
- [ ] Data encryption at rest
- [ ] Access controls implemented
- [ ] Incident response plan
- [ ] Regular security audits

---

## Summary

Choose deployment based on needs:

- **Testing**: Local network
- **Small scale**: Single VPS (DigitalOcean/AWS)
- **Production**: VPS + HTTPS + Nginx
- **Enterprise**: Load balanced + Database + Object storage

Always prioritize security and privacy compliance.
