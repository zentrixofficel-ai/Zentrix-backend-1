# ⚡ Zentrix Backend Platform

Zentrix Backend is a private Backend-as-a-Service (BaaS) and cloud infrastructure management platform built for modern Android applications, websites, and games.

## Core Capabilities
- **Multi-Project Isolation**: Separate databases, storage buckets, auth tenants, and credentials for projects like *Zentrix Esports*, *Zentrix Store*, *Zentrix Calculator*, and *Zentrix Official*.
- **Authentication**: Email/Password, Phone OTP, Google Sign-In, Anonymous auth, session & device management.
- **Scalable Document Database**: Collections, Documents, JSON Import/Export, and Zero-Trust Security Rules.
- **Zentrix Storage**: Image, video, audio, APK, and ZIP asset management with Expiring Signed URLs.
- **Realtime System**: WebSocket presence, live listeners, typing indicators, and message counters.
- **Push Notifications**: Topic targeting, device tokens, and scheduled campaigns.
- **Cloud Functions**: Deployable server-side triggers (HTTP, Database, Storage, Schedule).
- **Task Scheduler**: Cron jobs, recurring tasks, and background execution.
- **Vault Secrets**: AES-256 masked key vault for ImgBB, payment gateways, and third-party APIs.
- **Security Center**: Continuous security score evaluation (0-100%), rate limiting, brute-force protection, and audit logs.

## Quick Start
```bash
# Run standalone server
python3 backend/server.py

# Or run with Docker
docker-compose up -d
```
API runs on `http://localhost:8080` (Health endpoint: `http://localhost:8080/health`).
