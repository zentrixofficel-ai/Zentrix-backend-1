import time
import uuid
from backend.app.database import get_connection

def deploy_function(project_id: str, name: str, trigger_type: str, code: str, runtime: str = "python3.11") -> dict:
    conn = get_connection()
    cur = conn.cursor()
    fid = f"fn_{uuid.uuid4().hex[:10]}"
    now = time.time()
    cur.execute("""
        INSERT INTO functions (id, project_id, name, trigger_type, runtime, status, code, created_at)
        VALUES (?, ?, ?, ?, ?, 'DEPLOYED', ?, ?)
    """, (fid, project_id, name, trigger_type, runtime, code, now))
    conn.commit()
    conn.close()
    return {
        "id": fid,
        "name": name,
        "trigger_type": trigger_type,
        "runtime": runtime,
        "status": "DEPLOYED",
        "created_at": now
    }

def list_functions(project_id: str) -> list:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT id, name, trigger_type, runtime, status, execution_count, created_at FROM functions WHERE project_id = ? ORDER BY created_at DESC", (project_id,))
    rows = [dict(r) for r in cur.fetchall()]
    conn.close()
    return rows

def invoke_function(function_id: str, payload: dict = None) -> dict:
    conn = get_connection()
    cur = conn.cursor()
    cur.execute("SELECT * FROM functions WHERE id = ?", (function_id,))
    fn = cur.fetchone()
    if not fn:
        conn.close()
        raise ValueError("Function not found")
    
    cur.execute("UPDATE functions SET execution_count = execution_count + 1 WHERE id = ?", (function_id,))
    conn.commit()
    conn.close()

    return {
        "execution_id": f"exec_{uuid.uuid4().hex[:12]}",
        "function_name": fn["name"],
        "status": "SUCCESS",
        "latency_ms": 48.5,
        "result": {"message": f"Execution completed for {fn['name']}", "input": payload or {}}
    }
