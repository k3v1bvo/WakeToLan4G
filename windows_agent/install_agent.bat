@echo off
chcp 65001 >nul
title Instalador WakeToLan4G Windows Companion Agent
echo ========================================================
echo    Instalando WakeToLan4G Companion Agent para Windows
echo ========================================================
echo.

:: 1. Instalar dependencias Python
echo [*] Verificando dependencias de Python (psutil, pycaw, comtypes)...
python -m pip install --quiet --upgrade psutil pycaw comtypes
if %ERRORLEVEL% EQU 0 (
    echo [+] Dependencias instaladas correctamente.
) else (
    echo [!] Advertencia al instalar pycaw/comtypes. El agente usará fallback nativo.
)

:: 2. Configurar Regla en el Firewall de Windows para el puerto 8080
echo [*] Abriendo puerto 8080 en el Firewall de Windows...
netsh advfirewall firewall show rule name="WakeToLan4G Agent" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    netsh advfirewall firewall add rule name="WakeToLan4G Agent" dir=in action=allow protocol=TCP localport=8080 >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        echo [+] Puerto 8080 habilitado en el Firewall.
    ) else (
        echo [!] Nota: Si el firewall solicita permisos de administrador, acéptalos.
    )
) else (
    echo [+] Regla de firewall ya existente.
)

:: 3. Crear acceso directo en el Inicio de Windows
echo [*] Registrando agente en el Inicio de Windows...
set SCRIPT_DIR=%~dp0
set VBS_TARGET=%SCRIPT_DIR%start_agent.vbs
set STARTUP_DIR=%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup

powershell -NoProfile -ExecutionPolicy Bypass -Command "$ws = New-Object -ComObject WScript.Shell; $s = $ws.CreateShortcut('%STARTUP_DIR%\WakeToLan4G_Agent.lnk'); $s.TargetPath = 'wscript.exe'; $s.Arguments = '\"%VBS_TARGET%\"'; $s.WorkingDirectory = '%SCRIPT_DIR%'; $s.Save()"

if %ERRORLEVEL% EQU 0 (
    echo [+] Acceso directo creado en: %STARTUP_DIR%\WakeToLan4G_Agent.lnk
) else (
    echo [!] No se pudo registrar en Inicio automáticamente.
)

:: 4. Iniciar agente en segundo plano
echo [*] Iniciando agente en segundo plano...
wscript.exe "%VBS_TARGET%"

echo.
echo ========================================================
echo  ¡Listo! El agente de Windows está activo en puerto 8080.
echo  Se iniciará automáticamente cada vez que prendas tu PC.
echo ========================================================
pause
