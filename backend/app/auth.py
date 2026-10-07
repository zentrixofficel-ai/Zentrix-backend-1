import hashlib
import hmac
import time
import uuid
import json
from backend.app.config import config
from backend.app.database import get_connection

def hash_password(password: str, salt: str = None) -> tuple[str, str]:
    if not salt:
        salt = uuid.uuid4().hex[:16]
    hashed = hashlib.sha256((password + salt).encode('utf-8')).hexdigest()
    return f"{salt}${hashed}", salt

def verify_password(password: str, stored_hash: str) -> bool:
    if not stored_hash or "$" not in stored_hash:
        return False
    salt, hash_val = stored_hash.split("$", 1)
    test_hash = hashlib.sha256((password + salt).encode('utf-8')).hexdigest()
    return hmac.compare_digest(hash_val, test_hash)

def generate_token(uid: str, project_id: str, role: str) -> str:
    timestamp = str(int(time.time()))
    payload = f"{uid}:{project_id}:{role}:{timestamp}"
    sig = hmac.new(config.jwt_secret.encode('utf-8'), payload.encode('utf-8'), hashlib.sha256).hexdigest()
    return f"zx_tok_{payload}_{sig[:16]}"

def create_user(project_id: str, email: str, password: str = None, phone: str = None, name: str = None, role: str = "USER", provider: str = "email") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    uid = f"usr_{uuid.uuid4().hex[:12]}"
    pwd_hash = hash_password(password)[0] if password else ""
    now = time.time()
    display_name = name or (email.split("@")[0] if email else phone or "User")

    cur.execute("""
        INSERT INTO users (uid, project_id, email, phone, name, password_hash, provider, role, status, is_verified, created_at, last_login)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE', 1, ?, ?)
    """, (uid, project_id, email, phone, display_name, pwd_hash, provider, role, now, now))
    conn.commit()
    conn.close()
    return {
        "uid": uid,
        "project_id": project_id,
        "email": email,
        "phone": phone,
        "name": display_name,
        "role": role,
        "provider": provider,
        "status": "ACTIVE",
        "created_at": now
    }

def login_user(project_id: str, email: str, password: str, device_name: str = "Android Device") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM users WHERE project_id = ? AND email = ?", (project_id, email))
    row = cur.fetchone()
    if not row:
        conn.close()
        raise ValueError("Invalid user credentials or project")
    
    if row["status"] != "ACTIVE":
        conn.close()
        raise ValueError("User account is disabled or blocked")

    if row["password_hash"] and not verify_password(password, row["password_hash"]):
        conn.close()
        raise ValueError("Invalid password")

    now = time.time()
    cur.execute("UPDATE users SET last_login = ? WHERE uid = ?", (now, row["uid"]))
    token = generate_token(row["uid"], project_id, row["role"])
    
    # Register session
    cur.execute("""
        INSERT OR REPLACE INTO sessions (token, uid, project_id, device_name, created_at, expires_at)
        VALUES (?, ?, ?, ?, ?, ?)
    """, (token, row["uid"], project_id, device_name, now, now + (config.token_expiry_hours * 3600)))
    conn.commit()
    conn.close()

    return {
        "token": token,
        "uid": row["uid"],
        "email": row["email"],
        "name": row["name"],
        "role": row["role"],
        "project_id": project_id
    }

def set_user_status(uid: str, status: str):
    conn = get_connection()
    conn.execute("UPDATE users SET status = ? WHERE uid = ?", (status, uid))
    conn.commit()
    conn.close()

def list_users(project_id: str, limit: int = 50) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT uid, email, phone, name, provider, role, status, created_at, last_login FROM users WHERE project_id = ? ORDER BY created_at DESC LIMIT ?", (project_id, limit))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows
