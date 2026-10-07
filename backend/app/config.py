import os
from dataclasses import dataclass

@dataclass
class ZentrixConfig:
    platform_name: str = "Zentrix Backend"
    version: str = "1.0.0"
    environment: str = os.getenv("ZENTRIX_ENV", "production")
    host: str = os.getenv("ZENTRIX_HOST", "0.0.0.0")
    port: int = int(os.getenv("ZENTRIX_PORT", "8080"))
    db_path: str = os.getenv("ZENTRIX_DB_PATH", "zentrix_cluster.db")
    jwt_secret: str = os.getenv("ZENTRIX_JWT_SECRET", "zx_secret_platform_master_key_99420485")
    token_expiry_hours: int = 72
    storage_dir: str = os.getenv("ZENTRIX_STORAGE_DIR", "zentrix_storage")
    cors_origins: str = os.getenv("ZENTRIX_CORS_ORIGINS", "*")
    rate_limit_per_minute: int = 120
    master_admin_email: str = os.getenv("ZENTRIX_ADMIN_EMAIL", "admin@zentrix.io")

config = ZentrixConfig()
