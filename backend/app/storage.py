import time
import uuid
import hashlib
from backend.app.config import config
from backend.app.database import get_connection

def register_file(project_id: str, file_name: str, file_size: int, mime_type: str, folder: str = "/", is_public: bool = False) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    file_id = f"fil_{uuid.uuid4().hex[:12]}"
    now = time.time()
    storage_path = f"{project_id}{folder}{file_name}".replace("//", "/")

    cur.execute("""
        INSERT INTO storage_files (file_id, project_id, file_name, folder, file_size, mime_type, is_public, storage_path, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (file_id, project_id, file_name, folder, file_size, mime_type, 1 if is_public else 0, storage_path, now))
    conn.commit()
    conn.close()

    return {
        "file_id": file_id,
        "project_id": project_id,
        "file_name": file_name,
        "folder": folder,
        "file_size": file_size,
        "mime_type": mime_type,
        "is_public": is_public,
        "download_url": f"https://storage.zentrixcloud.io/v1/{storage_path}",
        "created_at": now
    }

def generate_signed_url(file_id: str, expiry_seconds: int = 3600) -> str:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM storage_files WHERE file_id = ?", (file_id,))
    row = cur.fetchone()
    conn.close()
    if not row:
        raise ValueError("File not found")
    
    expires_at = int(time.time()) + expiry_seconds
    signature = hashlib.sha256(f"{file_id}:{expires_at}:{config.jwt_secret}".encode()).hexdigest()[:16]
    return f"https://storage.zentrixcloud.io/signed/{row['storage_path']}?exp={expires_at}&sig={signature}"

def list_files(project_id: str, folder: str = "/") -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM storage_files WHERE project_id = ? AND folder = ? ORDER BY created_at DESC", (project_id, folder))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows

def delete_file(file_id: str):
    conn = get_connection()
    conn.execute("DELETE FROM storage_files WHERE file_id = ?", (file_id,))
    conn.commit()
    conn.close()
