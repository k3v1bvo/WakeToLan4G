# 📋 Data Inventory & Privacy ROPA (Record of Processing Activities)

Registro de actividades de procesamiento de datos e inventario técnico conforme a los principios de privacidad desde el diseño (Privacy by Design).

---

## 1. Inventario de Datos Técnicos Procesados

| Dato / Elemento | Tipo de Dato | Finalidad | Base Legal | Almacenamiento | Destinatarios / Transferencias |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Dirección MAC** (`70:4D:7B:87:92:53`) | Identificador de hardware de red | Construir el Paquete Mágico WoL (6x 0xFF + 16x MAC) | Ejecución técnica de la función solicitada | `SharedPreferences` local en el celular | Únicamente en el payload UDP local / WAN |
| **Dirección IP Local y Broadcast** (`192.168.1.x`) | Dato de enrutamiento | Envío de paquetes en la subred doméstica | Necesidad técnica | Local en el celular | Router y switch local |
| **Nombre de Dominio DDNS** (`duckdns.org`) | Registro DNS | Enrutamiento de paquetes por 4G | Necesidad técnica | Local en el celular | Servidores DNS públicos |
| **Nombre del Wi-Fi (SSID)** (`Xiaomi_911`) | Nombre de red inalámbrica | Conmutación automática entre modo Wi-Fi Casa vs 4G | Consentimiento / Configuración de usuario | Local en el celular | No se transmite fuera del dispositivo |
| **Telemetría de Hardware** (CPU, RAM, Disco) | Métricas de rendimiento | Mostrar el estado de la PC en el panel | Visualización solicitada por el usuario | Solo en memoria RAM volátil | Transmitido punto a punto PC <-> Móvil |
| **Nivel de Volumen Maestro** (0-100%) | Parámetro de sistema | Sincronización del slider de audio | Ejecución del comando de usuario | Solo en memoria RAM volátil | Transmitido punto a punto PC <-> Móvil |
| **Token Secreto de Seguridad** | Clave de autenticación | Proteger el agente Windows contra accesos no autorizados | Seguridad del sistema | Local en el celular y en `config.json` | Cabecera HTTP `Authorization: Bearer` local |

---

## 2. Principios de Protección de Datos

1. **Almacenamiento 100% Local (Zero Cloud Storage):** Ningún dato, identificador, contraseña ni dirección IP es enviado, almacenado ni respaldado en servidores en la nube de terceros.
2. **Sin Rastreo ni Telemetría Oculta:** La aplicación NO incluye SDKs de publicidad, Google Analytics, Firebase Analytics, Crashlytics ni rastreadores de comportamiento.
3. **Punto a Punto Directo (P2P):** La comunicación se realiza de forma directa entre el smartphone y la computadora personal del usuario a través de su red local o conexión directa DuckDNS.
