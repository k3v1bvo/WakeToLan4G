@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo Iniciando WakeToLan4G Agent en segundo plano...
start "" "C:\Users\PC\AppData\Local\Programs\Python\Python311\pythonw.exe" "%~dp0pc_agent.py"
echo [+] Agente iniciado.
timeout /t 2 >nul
