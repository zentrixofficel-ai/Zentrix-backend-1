import unittest
import os
import shutil
import tempfile
from backend.app.database import init_db, get_connection
from backend.app import projects, auth, database_service, storage, realtime, notifications, secrets, security, monitoring, admin

class TestZentrixBackend(unittest.TestCase):
    def setUp(self):
        self.test_dir = tempfile.mkdtemp()
        self.db_path = os.path.join(self.test_dir, "test_zentrix.db")
        init_db(self.db_path)
        # Patch config db path for tests
        from backend.app.config import config
        self.original_db = config.db_path
        config.db_path = self.db_path

    def tearDown(self):
        from backend.app.config import config
        config.db_path = self.original_db
        shutil.rmtree(self.test_dir, ignore_errors=True)

    def test_project_lifecycle(self):
        # 1. Create Project
        proj = projects.create_project(
            name="Zentrix Esports",
            project_id="zentrix-esport",
            description="Esports tournament server",
            environment="production"
        )
        self.assertEqual(proj["id"], "zentrix-esport")
        self.assertTrue(proj["client_key"].startswith("zx_pub_"))
        self.assertTrue(proj["server_key"].startswith("zx_sec_"))

        # 2. Register Android App
        app = projects.register_app(
            project_id="zentrix-esport",
            app_name="Zentrix Esports App",
            platform="android",
            package_name="com.zentrix.esport",
            sha1="AA:BB:CC:DD",
            sha256="EE:FF:00:11"
        )
        self.assertEqual(app["package_name"], "com.zentrix.esport")
        self.assertTrue(app["api_key"].startswith("zx_app_"))

        # 3. Generate Config
        cfg = projects.generate_app_config("zentrix-esport", app["app_id"])
        self.assertEqual(cfg["zentrix_project"]["project_id"], "zentrix-esport")
        self.assertEqual(cfg["client"]["package_name"], "com.zentrix.esport")

    def test_authentication_and_users(self):
        projects.create_project(name="Auth Test", project_id="p-auth")
        user = auth.create_user(
            project_id="p-auth",
            email="gamer@zentrix.io",
            password="SecurePassword123!",
            name="Apex Pro"
        )
        self.assertEqual(user["email"], "gamer@zentrix.io")
        self.assertEqual(user["role"], "USER")

        # Test login
        login_res = auth.login_user("p-auth", "gamer@zentrix.io", "SecurePassword123!")
        self.assertTrue(login_res["token"].startswith("zx_tok_"))

        # Test password verification failure
        with self.assertRaises(ValueError):
            auth.login_user("p-auth", "gamer@zentrix.io", "WrongPassword!")

    def test_database_documents(self):
        projects.create_project(name="DB Test", project_id="p-db")
        doc = database_service.set_document("p-db", "scores", "match-1", {"player": "Zentrix", "score": 9850})
        self.assertEqual(doc["doc_id"], "match-1")

        retrieved = database_service.get_document("p-db", "scores", "match-1")
        self.assertIsNotNone(retrieved)
        self.assertEqual(retrieved["data"]["score"], 9850)

    def test_secrets_and_security(self):
        projects.create_project(name="Security Test", project_id="p-sec")
        sec = secrets.add_secret("p-sec", "IMGBB_API_KEY", "secret_key_123456789", "Image Upload")
        self.assertIn("•", sec["masked_value"])

        score = security.calculate_security_score("p-sec")
        self.assertGreaterEqual(score["overall_score"], 80)

    def test_health_and_overview(self):
        projects.create_project(name="Health Test", project_id="p-hlth")
        health = monitoring.get_system_health()
        self.assertEqual(health["status"], "OPERATIONAL")
        self.assertIn("api_gateway", health["services"])

        overview = admin.get_cluster_overview()
        self.assertGreaterEqual(overview["total_projects"], 1)

if __name__ == "__main__":
    unittest.main()
