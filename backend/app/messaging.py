import time
import uuid
import json
from backend.app.database import get_connection

def create_conversation(project_id: str, name: str, conv_type: str = "direct", participants: list = None) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cid = f"conv_{uuid.uuid4().hex[:12]}"
    now = time.time()
    parts_json = json.dumps(participants or [])
    cur.execute("""
        INSERT INTO conversations (id, project_id, name, conv_type, participants_json, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
    """, (cid, project_id, name, conv_type, parts_json, now))
    conn.commit()
    conn.close()
    return {"id": cid, "name": name, "conv_type": conv_type, "participants": participants or [], "created_at": now}

def send_message(project_id: str, conversation_id: str, sender_id: str, content: str, msg_type: str = "text", media_url: str = "") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    mid = f"msg_{uuid.uuid4().hex[:14]}"
    now = time.time()
    cur.execute("""
        INSERT INTO messages (id, conversation_id, project_id, sender_id, content, msg_type, media_url, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """, (mid, conversation_id, project_id, sender_id, content, msg_type, media_url, now))
    conn.commit()
    conn.close()
    return {
        "id": mid,
        "conversation_id": conversation_id,
        "sender_id": sender_id,
        "content": content,
        "msg_type": msg_type,
        "media_url": media_url,
        "created_at": now
    }

def get_conversation_messages(conversation_id: str, limit: int = 50) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at ASC LIMIT ?", (conversation_id, limit))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows

def list_conversations(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM conversations WHERE project_id = ? ORDER BY created_at DESC", (project_id,))
    rows = []
    for r in cur.fetchall():
        d = dict(r)
        d["participants"] = json.loads(d.get("participants_json") or "[]")
        rows.append(d)
    conn.close()
    return rows
