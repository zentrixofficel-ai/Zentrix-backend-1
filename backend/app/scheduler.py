import time
import uuid
from backend.app.database import get_connection

def create_job(project_id: str, name: str, cron_expression: str, target_function: str = "") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    jid = f"job_{uuid.uuid4().hex[:10]}"
    now = time.time()
    cur.execute("""
        INSERT INTO jobs (id, project_id, name, cron_expression, target_function, status, created_at)
        VALUES (?, ?, ?, ?, ?, 'SCHEDULED', ?)
    """, (jid, project_id, name, cron_expression, target_function, now))
    conn.commit()
    conn.close()
    return {
        "id": jid,
        "name": name,
        "cron_expression": cron_expression,
        "status": "SCHEDULED",
        "created_at": now
    }

def list_jobs(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM jobs WHERE project_id = ? ORDER BY created_at DESC", (project_id,))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows
