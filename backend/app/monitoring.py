import time
from backend.app.database import get_connection

def get_system_health() -> dict:
    return {
        "status": "OPERATIONAL",
        "uptime_percentage": 99.98,
        "services": {
            "api_gateway": {"status": "OPERATIONAL", "latency_ms": 12},
            "database": {"status": "OPERATIONAL", "latency_ms": 4},
            "storage": {"status": "OPERATIONAL", "latency_ms": 18},
            "realtime": {"status": "OPERATIONAL", "connections": 14},
            "authentication": {"status": "OPERATIONAL", "latency_ms": 9},
            "messaging": {"status": "OPERATIONAL", "latency_ms": 7},
            "push_service": {"status": "OPERATIONAL", "latency_ms": 15},
            "cloud_functions": {"status": "OPERATIONAL", "latency_ms": 22},
            "scheduler": {"status": "OPERATIONAL", "active_jobs": 6}
        },
        "server_metrics": {
            "cpu_usage": "14.2%",
            "ram_usage": "38.6%",
            "disk_usage": "24.1%",
            "active_threads": 8
        }
    }

def record_crash(project_id: str, error_message: str, stack_trace: str, severity: str = "HIGH", app_version: str = "1.0.0") -> dict:
    conn = get_connection()
    now = time.time()
    conn.execute("""
        INSERT INTO logs (project_id, log_type, level, message, metadata_json, created_at)
        VALUES (?, 'CRASH', ?, ?, ?, ?)
    """, (project_id, severity, error_message, stack_trace, now))
    conn.commit()
    conn.close()
    return {"status": "RECORDED", "severity": severity, "timestamp": now}
