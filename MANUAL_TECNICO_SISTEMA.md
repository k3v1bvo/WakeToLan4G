# 📘 Manual Técnico Maestro del Sistema: WakeToLan4G & Red

Este documento contiene el registro exhaustivo de todas las configuraciones, scripts, rutas, credenciales y arquitectura implementadas en tu PC con Windows, la red doméstica y la aplicación móvil Android. Guárdalo como referencia definitiva para cualquier mantenimiento o consulta futura.

---

## 🖥️ 1. Ficha Técnica de Hardware y Red

| Parámetro | Detalle / Valor |
| :--- | :--- |
| **Nombre del Equipo (Hostname)** | `PCK3v1n` |
| **Placa Madre** | ASUS ROG STRIX H270F GAMING |
| **Tarjeta de Red Cableada** | Intel(R) Ethernet Connection (2) I219-V |
| **Dirección MAC** | `70:4D:7B:87:92:53` |
| **IP Fija Local de la PC** | `192.168.1.91` |
| **Máscara de Subred** | `255.255.255.0` |
| **IP de Difusión (Broadcast Local)** | `192.168.1.255` |
| **Puerta de Enlace (Router Comteco)** | `192.168.1.1` (Canal Wi-Fi: 6) |
| **Router Secundario (Xiaomi Mi 4C)** | `192.168.1.150` (Modo AP / Repetidor Cableado, Canal: 11) |
| **Repetidor (TP-Link Extender)** | Subred `192.168.1.x` (Canal Wi-Fi: 1) |
| **Dominio Dinámico (DuckDNS)** | `k3v1bvopici.duckdns.org` |
| **Puerto Wake-on-LAN (WoL/WoW)** | UDP `9` |
| **Puerto Agente de Telemetría** | TCP `8080` |
| **Token Secreto de Seguridad** | `kevin2026` |

---

## ⚡ 2. Wake-on-LAN (Local) y Wake-on-WAN (Internet 4G)

### ¿Cómo funciona el encendido?
El encendido remoto se basa en un **Paquete Mágico UDP** compuesto por 6 bytes de `0xFF` seguidos de 16 repeticiones de la dirección MAC de la tarjeta de red (`70:4D:7B:87:92:53`).

### Configuraciones Críticas en la PC:
1. **BIOS / UEFI:**
   - En `Advanced Mode -> APM Configuration -> Power On By PCI-E`: configurado en **Enabled**.
2. **Propiedades de la Tarjeta de Red (Intel I219-V):**
   - `Wake on Magic Packet`: **Habilitado**.
   - `Permitir que este dispositivo reactive el equipo`: **Activado**.
3. **Inicio Rápido de Windows (Fast Startup):**
   - **Desactivado** (`HiberbootEnabled = 0` en `HKLM:\SYSTEM\CurrentControlSet\Control\Session Manager\Power`).
   - *Razón:* Si el inicio rápido de Windows está activado, Windows apaga el bus PCIe y la tarjeta de red no escucha el paquete mágico. Con `0`, el equipo apaga limpiamente a estado S5 manteniendo energía en la tarjeta.

### Port Forwarding en el Router Comteco:
- **Protocolo:** UDP
- **Puerto WAN:** 9
- **IP Interna:** `192.168.1.91`
- **Puerto LAN:** 9

> Para probar el encendido por **4G**, debes apagar el Wi-Fi en el celular y usar tus datos móviles. Si estás dentro del mismo Wi-Fi e intentas enviar el paquete a tu IP pública/DuckDNS, el router no lo reenvía internamente debido a la falta de *NAT Loopback* en routers residenciales de fibra.

---

## 🦆 3. Actualizador Automático de DuckDNS

Para asegurar que tu dominio DuckDNS siempre apunte a la IP pública correcta de Comteco (que cambia periódicamente):

* **Carpeta de Trabajo:** `C:\Users\PC\.duckdns`
* **Script de actualización:** `C:\Users\PC\.duckdns\update.ps1`
  - Realiza un llamado silencioso a:  
    `https://www.duckdns.org/update?domains=k3v1bvopici&token=a7c1b504-20b1-4bb2-b67c-9b596d05daeb&ip=`
  - Registra las actualizaciones en `C:\Users\PC\.duckdns\duckdns.log`.
* **Arranque en Windows:**  
  Ubicado en: `C:\Users\PC\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\duckdns_autoupdate.vbs`.  
  Se ejecuta automáticamente al iniciar sesión de forma invisible.

---

## 🤖 4. Windows Companion Agent (`windows_agent`)

Servidor HTTP de telemetría y ejecución de comandos remotos desarrollado específicamente para comunicarse con la app Android.

* **Ubicación del Código:** `D:\ProyectosCode\WakeToLan4G\windows_agent`
* **Puerto:** TCP `8080` (habilitado en el Firewall de Windows).
* **Autenticación:** Requiere cabecera HTTP: `Authorization: Bearer kevin2026`.

### Endpoints Disponibles:
| Método | Endpoint | Acción / Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/status` | Devuelve JSON con CPU %, RAM usada/total, Disco libre/total, volumen y mute |
| `POST` | `/api/power/sleep` | Pone la PC en suspensión modo reposo (S3) |
| `POST` | `/api/power/lock` | Bloquea la sesión de Windows (`Win + L`) |
| `POST` | `/api/power/restart` | Reinicia la PC de forma segura |
| `POST` | `/api/power/shutdown`| Apaga la PC por completo |
| `POST` | `/api/volume` | Cambia el volumen maestro (`{"volume": 0-100}`) |
| `POST` | `/api/volume/mute` | Alterna entre silenciado y sonido activo |
| `POST` | `/api/media/playpause` | Envía la pulsación de Play/Pausa multimedia |

### Control y Mantenimiento del Agente:
- **Arranque Automático:** Se creó el acceso directo `WakeToLan4G_Agent.lnk` en tu carpeta de Inicio de Windows (`shell:startup`) apuntando a `start_agent.vbs`. Se ejecuta en segundo plano con `pythonw.exe` sin abrir consolas.
- **Detener el Agente:** Doble clic en `D:\ProyectosCode\WakeToLan4G\windows_agent\stop_agent.bat`.
- **Reinstalar / Verificar:** Clic derecho y *Ejecutar como Administrador* en `D:\ProyectosCode\WakeToLan4G\windows_agent\install_agent.bat`.

---

## 📱 5. Aplicación Android: WakeToLan4G

* **Repositorio de Código:** [https://github.com/k3v1bvo/WakeToLan4G](https://github.com/k3v1bvo/WakeToLan4G)
* **Autor:** `k3v1bvo` (`k3v1bvo@gmail.com`)
* **Tecnología:** Kotlin + Jetpack Compose Material 3 (Tema Cyberpunk Neón).

### Descarga del APK compilado:
1. Accede a [GitHub Actions - WakeToLan4G](https://github.com/k3v1bvo/WakeToLan4G/actions).
2. Entra a la última compilación exitosa (**Build WakeToLan4G APK**).
3. En la sección **Artifacts**, descarga el archivo comprimido `WakeToLan4G-Debug-APK`.
4. Descomprímelo en tu celular e instala el `app-debug.apk`.

---

## 🚀 6. Optimización de Arranque de Windows Realizada

Para acelerar el inicio de la PC y evitar consumo excesivo de memoria RAM, se retiraron del arranque automático (`HKCU:\Software\Microsoft\Windows\CurrentVersion\Run`):
- `Steam`
- `EpicGamesLauncher`
- `Battle.net`
- `RiotClient`
- `Docker Desktop`
- `Nox` / `NoxMultiPlayer`
- `BlueStacks Services`
- `Teams`
- `AdobeCollabSync`
- Procesos en segundo plano de Google Chrome y Microsoft Edge

> Ningún programa o juego fue desinstalado. Todos siguen funcionando al 100% cuando los abras manualmente.
> Si algún día deseas restaurar el arranque automático anterior, solo debes hacer doble clic en el respaldo:
> `C:\Users\PC\.duckdns\backup_startup.reg`

---

## 🔑 7. Estrategia de Cuentas GitHub

| Cuenta | Correo | Función y Propósito |
| :--- | :--- | :--- |
| **`k3v1bvo` (Personal)** | `k3v1bvo@gmail.com` | **Tu identidad definitiva de desarrollador.** Todo el código que escribes, repositorios, proyectos personales, colaboraciones open source y estrellas se registran aquí. |
| **`k3v1bvoU` (Universidad)** | `Vok2017683@est.univalle.edu` | **Acceso a beneficios gratuitos.** Se usa para mantener activo el **GitHub Student Developer Pack** (GitHub Copilot gratis de por vida estudiantil). Solo se utiliza para iniciar sesión en la extensión de Copilot en tu editor. |

*Configuración Git en esta máquina:*
- `git config --global user.name "k3v1bvo"`
- `git config --global user.email "k3v1bvo@gmail.com"`
