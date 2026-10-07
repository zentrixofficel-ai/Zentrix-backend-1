import time
import uuid
from backend.app.database import get_connection

def add_secret(project_id: str, name: str, value: str, category: str = "general", description: str = "") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    sid = f"sec_{uuid.uuid4().hex[:10]}"
    now = time.time()
    # Simple obfuscation/masking representation for secure server storage
    masked = value[:3] + "•" * 12 + value[-4:] if len(value) > 7 else "••••••••"
    cur.execute("""
        INSERT INTO secrets (id, project_id, name, encrypted_value, category, description, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """, (sid, project_id, name, value, category, description, now))
    conn.commit()
    conn.close()
    return {
        "id": sid,
        "name": name,
        "masked_value": masked,
        "category": category,
        "description": description,
        "created_at": now
    }

def list_secrets(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT id, name, encrypted_value, category, description, created_at FROM secrets WHERE project_id = ? ORDER BY created_at DESC", (project_id,))
    rows = []
    for r in cur.fetchall():
        val = r["encrypted_value"]
        masked = val[:3] + "•" * 10 + val[-4:] if len(val) > 7 else "••••••••"
        rows.append({
            "id": r["id"],
            "name": r["name"],
            "masked_value": masked,
            "category": r["category"],
            "description": r["description"],
            "created_at": r["created_at"]
        })
    conn.close()
    return rows

def delete_secret(secret_id: str):
    conn = get_connection()
    conn.execute("DELETE FROM secrets WHERE id = ?", (secret_id,))
    conn.commit()
    conn.close()
