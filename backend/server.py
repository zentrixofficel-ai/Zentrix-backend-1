"""
Zentrix Backend Platform - Production Standalone Server
Runs on Python 3.11+ using standard library without external dependencies.
"""
import http.server
import json
import urllib.parse
from backend.app.config import config
from backend.app.database import init_db
from backend.app import (
    projects, auth, database_service, storage, realtime,
    messaging, notifications, functions, scheduler, webhooks,
    secrets, security, monitoring, backups, admin
)

class ZentrixRequestHandler(http.server.BaseHTTPRequestHandler):
    def _send_json(self, data, status=200):
        body = json.dumps(data).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Zentrix-Api-Key")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_OPTIONS(self):
        self._send_json({"status": "OK"})

    def do_GET(self):
        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path.rstrip("/")
        
        if path in ("", "/health"):
            self._send_json({"status": "OPERATIONAL", "service": "Zentrix Backend", "version": config.version})
            return

        if path == "/v1/overview":
            self._send_json(admin.get_cluster_overview())
            return

        if path == "/v1/system/health":
            self._send_json(monitoring.get_system_health())
            return

        if path == "/v1/projects":
            self._send_json(projects.list_projects())
            return

        parts = path.split("/")
        # /v1/projects/<id>
        if len(parts) == 4 and parts[1] == "v1" and parts[2] == "projects":
            proj = projects.get_project(parts[3])
            if proj:
                self._send_json(proj)
            else:
                self._send_json({"error": "Project not found"}, 404)
            return

        # /v1/projects/<id>/users
        if len(parts) == 5 and parts[2] == "projects" and parts[4] == "users":
            self._send_json(auth.list_users(parts[3]))
            return

        # /v1/projects/<id>/security
        if len(parts) == 5 and parts[2] == "projects" and parts[4] == "security":
            self._send_json(security.calculate_security_score(parts[3]))
            return

        # /v1/projects/<id>/secrets
        if len(parts) == 5 and parts[2] == "projects" and parts[4] == "secrets":
            self._send_json(secrets.list_secrets(parts[3]))
            return

        # /v1/projects/<id>/database/<col>
        if len(parts) == 6 and parts[2] == "projects" and parts[4] == "database":
            self._send_json(database_service.query_collection(parts[3], parts[5]))
            return

        self._send_json({"error": "Endpoint not found", "path": path}, 404)

    def do_POST(self):
        content_len = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_len).decode("utf-8") if content_len > 0 else "{}"
        try:
            payload = json.loads(body) if body else {}
        except Exception:
            payload = {}

        parsed = urllib.parse.urlparse(self.path)
        path = parsed.path.rstrip("/")
        parts = path.split("/")

        if path == "/v1/projects":
            res = projects.create_project(
                name=payload.get("name", "New Project"),
                project_id=payload.get("id"),
                description=payload.get("description", ""),
                environment=payload.get("environment", "production")
            )
            self._send_json(res, 201)
            return

        # /v1/projects/<id>/apps
        if len(parts) == 5 and parts[2] == "projects" and parts[4] == "apps":
            res = projects.register_app(
                project_id=parts[3],
                app_name=payload.get("app_name", "Android App"),
                platform=payload.get("platform", "android"),
                package_name=payload.get("package_name", ""),
                sha1=payload.get("sha1", ""),
                sha256=payload.get("sha256", "")
            )
            self._send_json(res, 201)
            return

        # /v1/projects/<id>/auth/register
        if len(parts) == 6 and parts[2] == "projects" and parts[4] == "auth" and parts[5] == "register":
            res = auth.create_user(
                project_id=parts[3],
                email=payload.get("email"),
                password=payload.get("password"),
                phone=payload.get("phone"),
                name=payload.get("name"),
                role=payload.get("role", "USER")
            )
            self._send_json(res, 201)
            return

        # /v1/projects/<id>/auth/login
        if len(parts) == 6 and parts[2] == "projects" and parts[4] == "auth" and parts[5] == "login":
            try:
                res = auth.login_user(
                    project_id=parts[3],
                    email=payload.get("email"),
                    password=payload.get("password")
                )
                self._send_json(res)
            except Exception as e:
                self._send_json({"error": str(e)}, 400)
            return

        # /v1/projects/<id>/database/<col>/<doc_id>
        if len(parts) == 7 and parts[2] == "projects" and parts[4] == "database":
            res = database_service.set_document(
                project_id=parts[3],
                collection=parts[5],
                doc_id=parts[6],
                data=payload
            )
            self._send_json(res, 200)
            return

        # /v1/projects/<id>/notifications
        if len(parts) == 5 and parts[2] == "projects" and parts[4] == "notifications":
            res = notifications.send_push_notification(
                project_id=parts[3],
                title=payload.get("title", "Alert"),
                body=payload.get("body", ""),
                target_type=payload.get("target_type", "topic"),
                target_value=payload.get("target_value", "all"),
                data=payload.get("data")
            )
            self._send_json(res, 201)
            return

        # /v1/projects/<id>/messages
        if len(parts) == 5 and parts[2] == "projects" and parts[4] == "messages":
            res = messaging.send_message(
                project_id=parts[3],
                conversation_id=payload.get("conversation_id", "general"),
                sender_id=payload.get("sender_id", "system"),
                content=payload.get("content", ""),
                msg_type=payload.get("msg_type", "text")
            )
            self._send_json(res, 201)
            return

        self._send_json({"error": "Endpoint not found or method not allowed", "path": path}, 404)

def run_server(host=config.host, port=config.port):
    init_db()
    server = http.server.HTTPServer((host, port), ZentrixRequestHandler)
    print(f"🚀 Zentrix Backend Platform active on http://{host}:{port}")
    server.serve_forever()

if __name__ == "__main__":
    run_server()
