@echo off
chcp 65001 >nul
echo Deteniendo instancias de WakeToLan4G Companion Agent...
powershell -NoProfile -Command "Get-CimInstance Win32_Process | Where-Object CommandLine -like '*pc_agent.py*' | ForEach-Object { Stop-Process -Id $_.ProcessId -Force }"
echo [+] Agente detenido correctamente.
timeout /t 2 >nul
