import time
import uuid
import hmac
import hashlib
from backend.app.database import get_connection

SUPPORTED_EVENTS = [
    "user.created", "user.updated", "user.deleted",
    "database.created", "database.updated", "database.deleted",
    "storage.uploaded", "storage.deleted",
    "message.sent", "payment.completed", "project.updated"
]

def register_webhook(project_id: str, url: str, event_type: str) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    wid = f"wh_{uuid.uuid4().hex[:10]}"
    secret = f"whsec_{uuid.uuid4().hex[:20]}"
    now = time.time()
    cur.execute("""
        INSERT INTO webhooks (id, project_id, url, event_type, secret, is_active, created_at)
        VALUES (?, ?, ?, ?, ?, 1, ?)
    """, (wid, project_id, url, event_type, secret, now))
    conn.commit()
    conn.close()
    return {
        "id": wid,
        "url": url,
        "event_type": event_type,
        "secret": secret,
        "created_at": now
    }

def list_webhooks(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT id, url, event_type, is_active, created_at FROM webhooks WHERE project_id = ? ORDER BY created_at DESC", (project_id,))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows
