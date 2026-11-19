#!/usr/bin/env python3
"""
WakeToLan4G Companion Agent for Windows
Lightweight HTTP telemetry and remote control server.
"""

import sys
import os
import json
import logging
import platform
import subprocess
import ctypes
from http.server import HTTPServer, BaseHTTPRequestHandler
from pathlib import Path

# Setup logging
LOG_DIR = Path(__file__).parent
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    handlers=[
        logging.StreamHandler(sys.stdout),
        logging.FileHandler(LOG_DIR / "agent.log", encoding="utf-8")
    ]
)

# Load configuration
CONFIG_FILE = LOG_DIR / "config.json"
DEFAULT_CONFIG = {
    "port": 8080,
    "secret_token": "kevin2026",
    "cors_enabled": True
}

if CONFIG_FILE.exists():
    try:
        with open(CONFIG_FILE, "r", encoding="utf-8") as f:
            CONFIG = {**DEFAULT_CONFIG, **json.load(f)}
    except Exception as e:
        logging.error(f"Error loading config.json, using defaults: {e}")
        CONFIG = DEFAULT_CONFIG
else:
    CONFIG = DEFAULT_CONFIG

PORT = int(CONFIG.get("port", 8080))
TOKEN = str(CONFIG.get("secret_token", "kevin2026")).strip()

# Check optional libraries
try:
    import psutil
    HAS_PSUTIL = True
except ImportError:
    HAS_PSUTIL = False

try:
    from comtypes import CLSCTX_ALL
    from pycaw.pycaw import AudioUtilities, IAudioEndpointVolume
    HAS_PYCAW = True
except Exception:
    HAS_PYCAW = False


def get_audio_volume():
    """Returns (volume_percent_int, is_muted_bool)"""
    if HAS_PYCAW:
        try:
            devices = AudioUtilities.GetSpeakers()
            interface = devices.Activate(IAudioEndpointVolume._iid_, CLSCTX_ALL, None)
            volume = ctypes.cast(interface, ctypes.POINTER(IAudioEndpointVolume))
            vol_scalar = volume.GetMasterVolumeLevelScalar()
            is_muted = bool(volume.GetMute())
            return int(round(vol_scalar * 100)), is_muted
        except Exception as e:
            logging.debug(f"pycaw error: {e}")
    return 50, False


def set_audio_volume(percent: int):
    """Sets master volume to target percentage (0-100)"""
    percent = max(0, min(100, int(percent)))
    if HAS_PYCAW:
        try:
            devices = AudioUtilities.GetSpeakers()
            interface = devices.Activate(IAudioEndpointVolume._iid_, CLSCTX_ALL, None)
            volume = ctypes.cast(interface, ctypes.POINTER(IAudioEndpointVolume))
            volume.SetMasterVolumeLevelScalar(percent / 100.0, None)
            return True
        except Exception as e:
            logging.error(f"pycaw set error: {e}")
    return False


def toggle_mute():
    """Toggles master audio mute"""
    if HAS_PYCAW:
        try:
            devices = AudioUtilities.GetSpeakers()
            interface = devices.Activate(IAudioEndpointVolume._iid_, CLSCTX_ALL, None)
            volume = ctypes.cast(interface, ctypes.POINTER(IAudioEndpointVolume))
            current = volume.GetMute()
            volume.SetMute(0 if current else 1, None)
            return True
        except Exception as e:
            logging.error(f"pycaw mute error: {e}")
    # Fallback via keybd_event (VK_VOLUME_MUTE = 0xAD)
    ctypes.windll.user32.keybd_event(0xAD, 0, 0, 0)
    ctypes.windll.user32.keybd_event(0xAD, 0, 2, 0)
    return True


def press_media_playpause():
    """Simulates Media Play / Pause key (VK_MEDIA_PLAY_PAUSE = 0xB3)"""
    ctypes.windll.user32.keybd_event(0xB3, 0, 0, 0)
    ctypes.windll.user32.keybd_event(0xB3, 0, 2, 0)
    return True


def lock_pc():
    """Locks Windows session (Win + L)"""
    return bool(ctypes.windll.user32.LockWorkStation())


def sleep_pc():
    """Puts PC into sleep / standby mode (S3)"""
    # Uses powrprof.dll SetSuspendState(0, 1, 0)
    subprocess.Popen("rundll32.exe powrprof.dll,SetSuspendState 0,1,0", shell=True)
    return True


def restart_pc():
    """Restarts PC after 3 seconds"""
    subprocess.Popen("shutdown /r /t 3", shell=True)
    return True


def shutdown_pc():
    """Shuts down PC completely after 3 seconds"""
    subprocess.Popen("shutdown /s /t 3", shell=True)
    return True


def get_system_telemetry():
    """Collects CPU, RAM, Disk, and Hostname info"""
    hostname = platform.node()
    cpu_percent = 0.0
    ram_used = 0.0
    ram_total = 16.0
    disk_free = 0.0
    disk_total = 0.0

    if HAS_PSUTIL:
        try:
            cpu_percent = psutil.cpu_percent(interval=0.1)
            vm = psutil.virtual_memory()
            ram_used = round((vm.total - vm.available) / (1024 ** 3), 1)
            ram_total = round(vm.total / (1024 ** 3), 1)

            disk = psutil.disk_usage("C:")
            disk_free = round(disk.free / (1024 ** 3), 1)
            disk_total = round(disk.total / (1024 ** 3), 1)
        except Exception as e:
            logging.error(f"Error gathering psutil stats: {e}")

    vol, muted = get_audio_volume()

    return {
        "online": True,
        "hostname": hostname,
        "cpuPercent": float(cpu_percent),
        "ramUsedGb": float(ram_used),
        "ramTotalGb": float(ram_total),
        "diskFreeGb": float(disk_free),
        "diskTotalGb": float(disk_total),
        "masterVolume": int(vol),
        "isMuted": bool(muted),
        "latencyMs": 0
    }


class AgentRequestHandler(BaseHTTPRequestHandler):
    def _send_cors_headers(self):
        if CONFIG.get("cors_enabled", True):
            self.send_header("Access-Control-Allow-Origin", "*")
            self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
            self.send_header("Access-Control-Allow-Headers", "Authorization, Content-Type")

    def _check_auth(self) -> bool:
        auth_header = self.headers.get("Authorization", "")
        expected = f"Bearer {TOKEN}"
        if auth_header.strip() != expected:
            logging.warning(f"Unauthorized request from {self.client_address[0]} (Header: '{auth_header}')")
            self.send_response(401)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"error": "Unauthorized: Invalid or missing Bearer token"}).encode("utf-8"))
            return False
        return True

    def do_OPTIONS(self):
        self.send_response(204)
        self._send_cors_headers()
        self.end_headers()

    def do_GET(self):
        if not self._check_auth():
            return

        path = self.path.split("?")[0]
        if path == "/api/status" or path == "/":
            data = get_system_telemetry()
            self.send_response(200)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps(data).encode("utf-8"))
        else:
            self.send_response(404)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"error": "Endpoint not found"}).encode("utf-8"))

    def do_POST(self):
        if not self._check_auth():
            return

        path = self.path.split("?")[0]
        content_length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_length).decode("utf-8") if content_length > 0 else "{}"

        try:
            payload = json.loads(body) if body.strip() else {}
        except Exception:
            payload = {}

        logging.info(f"POST {path} with payload: {payload} from {self.client_address[0]}")

        res = {"success": True, "message": "Command executed"}

        if path == "/api/power/sleep":
            sleep_pc()
            res["message"] = "PC entrando en suspensión"
        elif path == "/api/power/lock":
            lock_pc()
            res["message"] = "Sesión de PC bloqueada"
        elif path == "/api/power/restart":
            restart_pc()
            res["message"] = "Reiniciando PC en 3 segundos"
        elif path == "/api/power/shutdown":
            shutdown_pc()
            res["message"] = "Apagando PC en 3 segundos"
        elif path == "/api/volume":
            vol = payload.get("volume", 50)
            set_audio_volume(vol)
            res["message"] = f"Volumen ajustado a {vol}%"
        elif path == "/api/volume/mute":
            toggle_mute()
            res["message"] = "Estado de silencio alternado"
        elif path == "/api/media/playpause":
            press_media_playpause()
            res["message"] = "Play/Pausa enviado"
        else:
            self.send_response(404)
            self._send_cors_headers()
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.end_headers()
            self.wfile.write(json.dumps({"error": "Unknown command endpoint"}).encode("utf-8"))
            return

        self.send_response(200)
        self._send_cors_headers()
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.end_headers()
        self.wfile.write(json.dumps(res).encode("utf-8"))

    def log_message(self, format, *args):
        # Suppress default noisy stderr logging, rely on logging module
        pass


def run_server():
    server_address = ("0.0.0.0", PORT)
    httpd = HTTPServer(server_address, AgentRequestHandler)
    logging.info(f"==================================================")
    logging.info(f" WakeToLan4G Companion Agent escuchando en 0.0.0.0:{PORT}")
    logging.info(f" Token Secreto Activo: {TOKEN}")
    logging.info(f" Pycaw disponible: {HAS_PYCAW} | Psutil disponible: {HAS_PSUTIL}")
    logging.info(f"==================================================")
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        logging.info("Deteniendo agente...")
        httpd.server_close()


if __name__ == "__main__":
    run_server()
