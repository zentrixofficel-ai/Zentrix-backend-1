import sqlite3
import os
import json
import uuid
import time
from backend.app.config import config

def get_connection(db_path: str = None) -> sqlite3.Connection:
    path = db_path or config.db_path
    conn = sqlite3.connect(path, check_same_thread=False)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON")
    conn.execute("PRAGMA journal_mode = WAL")
    return conn

def init_db(db_path: str = None):
    conn = get_connection(db_path)
    cur = conn.cursor()

    # Projects
    cur.execute("""
    CREATE TABLE IF NOT EXISTS projects (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        description TEXT DEFAULT '',
        environment TEXT DEFAULT 'production',
        region TEXT DEFAULT 'ap-south-1',
        icon TEXT DEFAULT 'cloud',
        status TEXT DEFAULT 'active',
        is_archived INTEGER DEFAULT 0,
        created_at REAL NOT NULL,
        last_activity REAL NOT NULL,
        settings_json TEXT DEFAULT '{}'
    )
    """)

    # Apps
    cur.execute("""
    CREATE TABLE IF NOT EXISTS apps (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        app_name TEXT NOT NULL,
        platform TEXT NOT NULL,
        package_name TEXT DEFAULT '',
        bundle_id TEXT DEFAULT '',
        app_url TEXT DEFAULT '',
        sha1 TEXT DEFAULT '',
        sha256 TEXT DEFAULT '',
        api_key TEXT NOT NULL,
        app_secret TEXT NOT NULL,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Users
    cur.execute("""
    CREATE TABLE IF NOT EXISTS users (
        uid TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        email TEXT,
        phone TEXT,
        name TEXT,
        photo_url TEXT DEFAULT '',
        password_hash TEXT DEFAULT '',
        provider TEXT DEFAULT 'email',
        role TEXT DEFAULT 'USER',
        status TEXT DEFAULT 'ACTIVE',
        is_verified INTEGER DEFAULT 0,
        custom_claims_json TEXT DEFAULT '{}',
        metadata_json TEXT DEFAULT '{}',
        created_at REAL NOT NULL,
        last_login REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Sessions & Devices
    cur.execute("""
    CREATE TABLE IF NOT EXISTS sessions (
        token TEXT PRIMARY KEY,
        uid TEXT NOT NULL,
        project_id TEXT NOT NULL,
        device_id TEXT,
        device_name TEXT,
        ip_address TEXT,
        created_at REAL NOT NULL,
        expires_at REAL NOT NULL,
        FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE
    )
    """)

    # API Keys
    cur.execute("""
    CREATE TABLE IF NOT EXISTS api_keys (
        key_id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        name TEXT NOT NULL,
        key_type TEXT NOT NULL,
        key_value TEXT UNIQUE NOT NULL,
        is_active INTEGER DEFAULT 1,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Database Collections & Documents
    cur.execute("""
    CREATE TABLE IF NOT EXISTS documents (
        doc_id TEXT NOT NULL,
        project_id TEXT NOT NULL,
        collection_name TEXT NOT NULL,
        data_json TEXT NOT NULL,
        created_at REAL NOT NULL,
        updated_at REAL NOT NULL,
        PRIMARY KEY (project_id, collection_name, doc_id),
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Storage Files
    cur.execute("""
    CREATE TABLE IF NOT EXISTS storage_files (
        file_id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        file_name TEXT NOT NULL,
        folder TEXT DEFAULT '/',
        file_size INTEGER NOT NULL,
        mime_type TEXT NOT NULL,
        is_public INTEGER DEFAULT 0,
        storage_path TEXT NOT NULL,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Messaging: Conversations & Messages
    cur.execute("""
    CREATE TABLE IF NOT EXISTS conversations (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        name TEXT,
        conv_type TEXT DEFAULT 'direct',
        participants_json TEXT NOT NULL,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS messages (
        id TEXT PRIMARY KEY,
        conversation_id TEXT NOT NULL,
        project_id TEXT NOT NULL,
        sender_id TEXT NOT NULL,
        content TEXT NOT NULL,
        msg_type TEXT DEFAULT 'text',
        media_url TEXT DEFAULT '',
        reactions_json TEXT DEFAULT '{}',
        created_at REAL NOT NULL,
        FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
    )
    """)

    # Push Notifications
    cur.execute("""
    CREATE TABLE IF NOT EXISTS notifications (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        title TEXT NOT NULL,
        body TEXT NOT NULL,
        target_type TEXT DEFAULT 'topic',
        target_value TEXT DEFAULT 'all',
        data_json TEXT DEFAULT '{}',
        status TEXT DEFAULT 'SENT',
        sent_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Cloud Functions
    cur.execute("""
    CREATE TABLE IF NOT EXISTS functions (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        name TEXT NOT NULL,
        trigger_type TEXT NOT NULL,
        runtime TEXT DEFAULT 'python3.11',
        status TEXT DEFAULT 'DEPLOYED',
        code TEXT NOT NULL,
        execution_count INTEGER DEFAULT 0,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Scheduled Jobs
    cur.execute("""
    CREATE TABLE IF NOT EXISTS jobs (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        name TEXT NOT NULL,
        cron_expression TEXT NOT NULL,
        target_function TEXT,
        status TEXT DEFAULT 'SCHEDULED',
        last_run REAL DEFAULT 0,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Webhooks
    cur.execute("""
    CREATE TABLE IF NOT EXISTS webhooks (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        url TEXT NOT NULL,
        event_type TEXT NOT NULL,
        secret TEXT NOT NULL,
        is_active INTEGER DEFAULT 1,
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Vault Secrets
    cur.execute("""
    CREATE TABLE IF NOT EXISTS secrets (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        name TEXT NOT NULL,
        encrypted_value TEXT NOT NULL,
        category TEXT DEFAULT 'general',
        description TEXT DEFAULT '',
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Logs & Audit
    cur.execute("""
    CREATE TABLE IF NOT EXISTS logs (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        project_id TEXT NOT NULL,
        log_type TEXT NOT NULL,
        level TEXT NOT NULL,
        message TEXT NOT NULL,
        metadata_json TEXT DEFAULT '{}',
        created_at REAL NOT NULL
    )
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS audit_logs (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        project_id TEXT NOT NULL,
        actor TEXT NOT NULL,
        action TEXT NOT NULL,
        ip_address TEXT DEFAULT '127.0.0.1',
        details TEXT DEFAULT '',
        created_at REAL NOT NULL
    )
    """)

    # Feature Flags
    cur.execute("""
    CREATE TABLE IF NOT EXISTS feature_flags (
        id TEXT PRIMARY KEY,
        project_id TEXT NOT NULL,
        flag_key TEXT NOT NULL,
        is_enabled INTEGER DEFAULT 0,
        rollout_percentage INTEGER DEFAULT 100,
        description TEXT DEFAULT '',
        created_at REAL NOT NULL,
        FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
    )
    """)

    # Indexes
    cur.execute("CREATE INDEX IF NOT EXISTS idx_users_project ON users(project_id)")
    cur.execute("CREATE INDEX IF NOT EXISTS idx_docs_col ON documents(project_id, collection_name)")
    cur.execute("CREATE INDEX IF NOT EXISTS idx_logs_project ON logs(project_id)")
    cur.execute("CREATE INDEX IF NOT EXISTS idx_audit_project ON audit_logs(project_id)")

    conn.commit()
    conn.close()
