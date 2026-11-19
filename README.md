# ⚡ WakeToLan4G — PC Remote Command Center

**WakeToLan4G** es una suite completa para encender y controlar tu PC de forma remota, tanto desde tu red local Wi-Fi como a través de datos móviles **4G / Internet** usando **DuckDNS** y paquetes mágicos Wake-on-LAN (WoL/WoW).

Incluye una aplicación móvil nativa para Android en **Jetpack Compose (Cyberpunk Dark Mode)** y un **Agente Ligero para Windows** con telemetría en tiempo real y comandos de energía.

---

## 🚀 Características Principales

### 📱 Aplicación Android (WakeToLan4G)
- **Detección Automática de Red:** Cambia de forma transparente entre **Red Local (Wi-Fi)** usando broadcast `192.168.1.255` y **Red Móvil (4G/WAN)** usando DuckDNS (`k3v1bvopici.duckdns.org`).
- **Botón Central Pulsante (Hero Power):** Disparo instantáneo de paquetes mágicos WoL al pulsar el botón con animaciones neón.
- **Acciones Rápidas:**
  - ⚡ **WOL Local:** Difusión rápida en subnet local.
  - 🌐 **WOL 4G:** Encendido remoto por Internet a través de DDNS.
  - 💤 **Suspender PC:** Modo reposo bajo consumo (S3).
  - 🔒 **Bloquear:** Bloqueo de sesión instantáneo (`Win + L`).
  - 🔄 **Reiniciar:** Reboot seguro de Windows.
  - 🛑 **Apagar PC:** Diálogo de confirmación para apagado total.
- **Telemetría en Vivo:**
  - Indicador de estado **ONLINE / OFFLINE** con latencia en milisegundos (`ms`).
  - Porcentaje de uso de **CPU**.
  - Consumo de **Memoria RAM** en GB (`usado / total`).
  - Espacio disponible en disco SSD (`C:`).
- **Control de Audio y Medios:**
  - Slider interactivo para control del volumen maestro de Windows (0-100%).
  - Botón de silencio (Mute / Unmute).
  - Control de reproducción multimedia (Play / Pause).
- **Configuración Persistente:** Modifica MAC, IP de difusión, IP local, dominio DuckDNS, puertos y tokens en cualquier momento sin recompilar la app.

---

### 🖥️ Windows Companion Agent (`windows_agent/`)
- Servidor HTTP ligero en Python (puerto `8080`).
- **Seguridad:** Autenticación por token Bearer (`Authorization: Bearer kevin2026`).
- **Arranque Silencioso:** Ejecución en segundo plano mediante VBS (`pythonw.exe`) sin ventanas de consola visibles.
- **Autoarranque:** Script de instalación de un solo clic que lo registra en el inicio de Windows y configura el Firewall.

---

## 🛠️ Instalación y Uso

### 1. Iniciar el Agente en tu PC con Windows
1. Entra a la carpeta `windows_agent`.
2. Haz clic derecho en `install_agent.bat` y selecciona **"Ejecutar como Administrador"**.
3. El instalador:
   - Verificará e instalará las librerías necesarias (`psutil`, `pycaw`, `comtypes`).
   - Abrirá el puerto 8080 en el Firewall de Windows.
   - Creará el acceso directo en `Inicio` para que arranque solo al encender la PC.
   - Iniciará el servicio inmediatamente en segundo plano.

*(Para detener el agente cuando lo desees, ejecuta `windows_agent/stop_agent.bat`).*

---

### 2. Descargar e Instalar la App en Android
Cada vez que se sube un cambio a GitHub, **GitHub Actions** compila automáticamente el APK listo para instalar:
1. Dirígete a la pestaña **Actions** en este repositorio de GitHub.
2. Haz clic en la última ejecución del flujo **Build WakeToLan4G APK**.
3. En la sección **Artifacts**, descarga el archivo `WakeToLan4G-Debug-APK`.
4. Instala el APK en tu teléfono móvil.

---

## ⚙️ Configuración de Red Recomendada

| Parámetro | Valor por Defecto |
| :--- | :--- |
| **Dirección MAC** | `70:4D:7B:87:92:53` |
| **Broadcast Local** | `192.168.1.255` |
| **IP Local de la PC** | `192.168.1.91` |
| **Dominio Remoto (DDNS)**| `k3v1bvopici.duckdns.org` |
| **Puerto Wake-on-LAN** | `9` (UDP) |
| **Puerto Agente Windows** | `8080` (TCP) |
| **Token Secreto** | `kevin2026` |
| **SSID Wi-Fi de Casa** | `Xiaomi_911` |

---

## 🛡️ Licencia y Propiedad Intelectual
**Software Propietario y Confidencial — Todos los derechos reservados.**  
Copyright © 2026 **Kevin Vergara Orellana ([k3v1bvo](https://github.com/k3v1bvo))**.

Queda estrictamente prohibida la copia, redistribución, modificación o comercialización no autorizada del código fuente, arquitectura o ejecutables (APK) de este proyecto. Consulta el archivo [LICENSE](LICENSE) para más detalles legales.
