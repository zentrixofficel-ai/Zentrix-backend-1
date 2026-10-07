import time
import uuid
import json
from backend.app.database import get_connection

def send_push_notification(project_id: str, title: str, body: str, target_type: str = "topic", target_value: str = "all", data: dict = None) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    nid = f"notif_{uuid.uuid4().hex[:12]}"
    now = time.time()
    cur.execute("""
        INSERT INTO notifications (id, project_id, title, body, target_type, target_value, data_json, status, sent_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, 'SENT', ?)
    """, (nid, project_id, title, body, target_type, target_value, json.dumps(data or {}), now))
    conn.commit()
    conn.close()
    return {
        "id": nid,
        "project_id": project_id,
        "title": title,
        "body": body,
        "status": "SENT",
        "target": f"{target_type}:{target_value}",
        "sent_at": now
    }

def list_notifications(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM notifications WHERE project_id = ? ORDER BY sent_at DESC LIMIT 50", (project_id,))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows
