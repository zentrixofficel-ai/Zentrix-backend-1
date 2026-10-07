import time
import json
import uuid
from backend.app.database import get_connection

def set_document(project_id: str, collection: str, doc_id: str, data: dict) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    now = time.time()
    data_str = json.dumps(data)

    cur.execute("""
        INSERT INTO documents (doc_id, project_id, collection_name, data_json, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT(project_id, collection_name, doc_id) DO UPDATE SET
            data_json = excluded.data_json,
            updated_at = excluded.updated_at
    """, (doc_id, project_id, collection, data_str, now, now))
    conn.commit()
    conn.close()
    return {"doc_id": doc_id, "collection": collection, "data": data, "updated_at": now}

def get_document(project_id: str, collection: str, doc_id: str) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM documents WHERE project_id = ? AND collection_name = ? AND doc_id = ?",
                (project_id, collection, doc_id))
    row = cur.fetchone()
    conn.close()
    if not row:
        return None
    return {
        "doc_id": row["doc_id"],
        "collection": row["collection_name"],
        "data": json.loads(row["data_json"]),
        "created_at": row["created_at"],
        "updated_at": row["updated_at"]
    }

def delete_document(project_id: str, collection: str, doc_id: str):
    conn = get_connection()
    conn.execute("DELETE FROM documents WHERE project_id = ? AND collection_name = ? AND doc_id = ?",
                 (project_id, collection, doc_id))
    conn.commit()
    conn.close()

def list_collections(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT DISTINCT collection_name FROM documents WHERE project_id = ?", (project_id,))
    cols = [r[0] for r in cur.fetchall()]
    conn.close()
    return cols

def query_collection(project_id: str, collection: str, limit: int = 50) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM documents WHERE project_id = ? AND collection_name = ? ORDER BY updated_at DESC LIMIT ?",
                (project_id, collection, limit))
    rows = cur.fetchall()
    conn.close()
    res = []
    for r in rows:
        res.append({
            "doc_id": r["doc_id"],
            "collection": r["collection_name"],
            "data": json.loads(r["data_json"]),
            "updated_at": r["updated_at"]
        })
    return res

def export_collection_json(project_id: str, collection: str) -> str:
    docs = query_collection(project_id, collection, limit=1000)
    return json.dumps(docs, indent=2)

def import_collection_json(project_id: str, collection: str, json_str: str) -> int:
    docs = json.loads(json_str)
    count = 0
    for d in docs:
        doc_id = d.get("doc_id") or f"doc_{uuid.uuid4().hex[:8]}"
        payload = d.get("data") if "data" in d else d
        set_document(project_id, collection, doc_id, payload)
        count += 1
    return count
