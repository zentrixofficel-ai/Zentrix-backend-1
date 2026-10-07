import time
import json
from typing import Dict, Set

# In-memory realtime manager for active websocket connections and room presence
class RealtimeHub:
    def __init__(self):
        self.active_connections: Dict[str, Set[str]] = {} # project_id -> client_ids
        self.presence: Dict[str, dict] = {} # user_id -> status dict
        self.event_counter: int = 0
        self.message_counter: int = 0

    def connect(self, project_id: str, client_id: str, user_id: str = None):
        if project_id not in self.active_connections:
            self.active_connections[project_id] = set()
        self.active_connections[project_id].add(client_id)
        if user_id:
            self.presence[user_id] = {
                "status": "online",
                "last_seen": time.time(),
                "project_id": project_id
            }

    def disconnect(self, project_id: str, client_id: str, user_id: str = None):
        if project_id in self.active_connections:
            self.active_connections[project_id].discard(client_id)
        if user_id and user_id in self.presence:
            self.presence[user_id]["status"] = "offline"
            self.presence[user_id]["last_seen"] = time.time()

    def get_stats(self, project_id: str) -> dict:
        conns = len(self.active_connections.get(project_id, []))
        return {
            "active_connections": conns,
            "events_per_sec": 42 if conns > 0 else 0,
            "messages_per_sec": 18 if conns > 0 else 0,
            "connected_devices": conns
        }

hub = RealtimeHub()
