# 🛡️ Política de Privacidad de WakeToLan4G

**Última actualización:** 19 de noviembre de 2026  
**Responsable del Tratamiento:** Kevin Vergara Orellana (`k3v1bvo`)  
**Contacto:** `k3v1bvo@gmail.com`

---

## 1. Introducción y Compromiso
`WakeToLan4G` ha sido diseñado bajo el principio estricto de **Privacidad por Diseño (Privacy by Design)**. Esta aplicación y su agente de Windows asociado operan como una herramienta de utilidad técnica privada y directa, garantizando que el usuario mantiene el control absoluto y exclusivo sobre su información y dispositivos.

---

## 2. Datos Procesados y Finalidad
La aplicación únicamente procesa los datos estrictamente necesarios para su funcionamiento técnico:
- **Dirección MAC de red:** Necesaria para ensamblar y emitir el paquete mágico Wake-on-LAN.
- **Direcciones IP (Local, Broadcast) y Dominio DDNS:** Utilizados exclusivamente para enrutar los paquetes UDP y solicitudes HTTP hacia su propia computadora.
- **Nombre de red Wi-Fi (SSID):** Utilizado de forma local para alternar automáticamente entre la configuración de red doméstica y datos móviles (4G).
- **Métricas de Rendimiento (CPU, RAM, Disco):** Consultadas en tiempo real bajo demanda y mostradas exclusivamente en la pantalla de su dispositivo.

**Finalidad exclusiva:** Ejecución del encendido remoto, apagado, bloqueo y monitoreo de la computadora personal del usuario a solicitud explícita de este.

---

## 3. Base Legal del Tratamiento
El tratamiento de estos datos técnicos se fundamenta en la **ejecución de la funcionalidad solicitada directamente por el usuario** (Art. 6.1.b RGPD / regulaciones equivalentes) y en el **consentimiento explícito** al configurar voluntariamente los parámetros de conexión en la aplicación.

---

## 4. Almacenamiento, Retención y Seguridad
- **Almacenamiento Local Exclusivo:** Todos los parámetros configurados (MAC, IPs, token) se almacenan de forma local en la memoria protegida de su dispositivo Android (`SharedPreferences`) y en el archivo local de su PC (`config.json`).
- **Cero Servidores en la Nube:** No disponemos de servidores centrales, bases de datos remotas ni almacenamiento en la nube donde se recopilen sus datos.
- **Retención:** Los datos se conservan en su dispositivo únicamente hasta que usted decida modificarlos o desinstalar la aplicación.

---

## 5. Cookies y Rastreo de Terceros
- **Sin Cookies:** La aplicación y el agente no emplean cookies de ningún tipo.
- **Sin Rastreadores:** No se incluyen SDKs de publicidad, analíticas de comportamiento (Google Analytics, Firebase, Facebook SDK) ni rastreadores de terceros.

---

## 6. Proveedores y Terceros
El único servicio externo involucrado es el proveedor de DNS dinámico **DuckDNS** (únicamente si el usuario decide emplear dicha funcionalidad para conexiones fuera de casa). DuckDNS únicamente recibe la dirección IP pública del enrutador del usuario para actualizar el registro de nombres DNS, sin acceso a credenciales, telemetría ni datos del equipo.

---

## 7. Derechos del Usuario
Usted tiene derecho en cualquier momento a:
- **Acceso y Rectificación:** Consultar y modificar cualquier dato configurado en la pantalla de Ajustes de la aplicación.
- **Supresión:** Eliminar todos los datos almacenados simplemente borrando los datos de la aplicación en los Ajustes de Android o desinstalándola.
- **Control:** Revocar o cambiar en cualquier momento el token de seguridad y las credenciales de conexión.

Para cualquier duda relativa a esta política, puede comunicarse directamente con el autor a través de: `k3v1bvo@gmail.com`.
