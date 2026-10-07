import time
import json
import uuid
from backend.app.database import get_connection

def create_project_backup(project_id: str) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM projects WHERE id = ?", (project_id,))
    p = cur.fetchone()
    cur.execute("SELECT * FROM documents WHERE project_id = ?", (project_id,))
    docs = [dict(r) for r in cur.fetchall()]
    cur.execute("SELECT * FROM users WHERE project_id = ?", (project_id,))
    users = [dict(r) for r in cur.fetchall()]
    conn.close()

    backup_id = f"bkg_{uuid.uuid4().hex[:10]}"
    now = time.time()
    payload = {
        "backup_id": backup_id,
        "project_id": project_id,
        "created_at": now,
        "record_counts": {
            "documents": len(docs),
            "users": len(users)
        }
    }
    return payload
