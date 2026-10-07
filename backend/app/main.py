"""
Zentrix Backend Platform - FastAPI Production Entrypoint
"""
try:
    from fastapi import FastAPI, HTTPException, Request, Depends
    from fastapi.middleware.cors import CORSMiddleware
    from pydantic import BaseModel
    FASTAPI_AVAILABLE = True
except ImportError:
    FASTAPI_AVAILABLE = False

from backend.app.config import config
from backend.app.database import init_db
from backend.app import projects, auth, database_service, storage, realtime, notifications, security, monitoring, admin

if FASTAPI_AVAILABLE:
    app = FastAPI(
        title="Zentrix Backend Platform",
        version=config.version,
        description="Private Backend-as-a-Service for Mobile Apps & Websites"
    )

    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    @app.on_event("startup")
    def startup_db():
        init_db()

    @app.get("/health")
    def health_check():
        return {"status": "OPERATIONAL", "service": "Zentrix Backend", "version": config.version}

    @app.get("/v1/overview")
    def get_overview():
        return admin.get_cluster_overview()

    @app.get("/v1/system/health")
    def get_health():
        return monitoring.get_system_health()

    @app.get("/v1/projects")
    def get_all_projects():
        return projects.list_projects()

    @app.post("/v1/projects")
    def create_new_project(req: dict):
        return projects.create_project(
            name=req.get("name", "New Project"),
            project_id=req.get("id"),
            description=req.get("description", ""),
            environment=req.get("environment", "production")
        )

    @app.get("/v1/projects/{project_id}")
    def get_single_project(project_id: str):
        p = projects.get_project(project_id)
        if not p:
            raise HTTPException(status_code=404, detail="Project not found")
        return p

else:
    app = None
