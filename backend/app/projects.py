import time
import uuid
import json
from backend.app.database import get_connection

def create_project(name: str, project_id: str = None, description: str = "", environment: str = "production", region: str = "ap-south-1") -> dict:
    pid = project_id or name.lower().replace(" ", "-").replace("_", "-")
    now = time.time()
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("""
        INSERT INTO projects (id, name, description, environment, region, status, created_at, last_activity)
        VALUES (?, ?, ?, ?, ?, 'active', ?, ?)
    """, (pid, name, description, environment, region, now, now))

    # Auto-generate server and client API keys for project
    client_key = f"zx_pub_{uuid.uuid4().hex[:20]}"
    server_key = f"zx_sec_{uuid.uuid4().hex[:24]}"
    cur.execute("INSERT INTO api_keys (key_id, project_id, name, key_type, key_value, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                (f"key_{uuid.uuid4().hex[:8]}", pid, "Default Client Key", "public", client_key, now))
    cur.execute("INSERT INTO api_keys (key_id, project_id, name, key_type, key_value, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                (f"key_{uuid.uuid4().hex[:8]}", pid, "Master Server Key", "server", server_key, now))

    # Log audit
    cur.execute("INSERT INTO audit_logs (project_id, actor, action, details, created_at) VALUES (?, 'System', 'PROJECT_CREATED', ?, ?)",
                (pid, f"Project {name} created with environment {environment}", now))

    conn.commit()
    conn.close()
    return {
        "id": pid,
        "name": name,
        "description": description,
        "environment": environment,
        "region": region,
        "status": "active",
        "client_key": client_key,
        "server_key": server_key,
        "created_at": now
    }

def list_projects() -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM projects WHERE is_archived = 0 ORDER BY created_at DESC")
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows

def get_project(project_id: str) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM projects WHERE id = ?", (project_id,))
    row = cur.fetchone()
    if not row:
        conn.close()
        return None
    res = dict(row)
    cur.execute("SELECT * FROM apps WHERE project_id = ?", (project_id,))
    res["apps"] = [dict(a) for a in cur.fetchall()]
    cur.execute("SELECT * FROM api_keys WHERE project_id = ?", (project_id,))
    res["api_keys"] = [dict(k) for k in cur.fetchall()]
    conn.close()
    return res

def register_app(project_id: str, app_name: str, platform: str, package_name: str = "", sha1: str = "", sha256: str = "") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    app_id = f"app_{uuid.uuid4().hex[:12]}"
    api_key = f"zx_app_{uuid.uuid4().hex[:18]}"
    secret = f"zx_secret_{uuid.uuid4().hex[:24]}"
    now = time.time()
    cur.execute("""
        INSERT INTO apps (id, project_id, app_name, platform, package_name, sha1, sha256, api_key, app_secret, created_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, (app_id, project_id, app_name, platform, package_name, sha1, sha256, api_key, secret, now))
    conn.commit()
    conn.close()
    return {
        "app_id": app_id,
        "project_id": project_id,
        "app_name": app_name,
        "platform": platform,
        "package_name": package_name,
        "api_key": api_key,
        "created_at": now
    }

def generate_app_config(project_id: str, app_id: str) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM projects WHERE id = ?", (project_id,))
    p = cur.fetchone()
    cur.execute("SELECT * FROM apps WHERE id = ?", (app_id,))
    a = cur.fetchone()
    conn.close()
    if not p or not a:
        raise ValueError("Project or App not found")
    
    return {
        "zentrix_project": {
            "project_id": p["id"],
            "project_name": p["name"],
            "environment": p["environment"],
            "region": p["region"]
        },
        "client": {
            "app_id": a["id"],
            "app_name": a["app_name"],
            "platform": a["platform"],
            "package_name": a["package_name"],
            "api_key": a["api_key"]
        },
        "endpoints": {
            "api": "https://api.zentrixcloud.io/v1",
            "realtime_ws": "wss://ws.zentrixcloud.io/realtime",
            "storage": "https://storage.zentrixcloud.io"
        }
    }

def archive_project(project_id: str):
    conn = get_connection()
    conn.execute("UPDATE projects SET is_archived = 1 WHERE id = ?", (project_id,))
    conn.commit()
    conn.close()

def restore_project(project_id: str):
    conn = get_connection()
    conn.execute("UPDATE projects SET is_archived = 0 WHERE id = ?", (project_id,))
    conn.commit()
    conn.close()

def delete_project(project_id: str):
    conn = get_connection()
    conn.execute("DELETE FROM projects WHERE id = ?", (project_id,))
    conn.commit()
    conn.close()
