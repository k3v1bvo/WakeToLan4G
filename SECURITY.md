# 🔒 Security Policy / Política de Seguridad

## 1. Supported Versions / Versiones Soportadas

Actualmente se proporciona soporte y correcciones de seguridad para las siguientes versiones:

| Versión | Soportada |
| :--- | :--- |
| `1.0.x` (Última versión en `main`) | :white_check_mark: Sí |
| Versiones anteriores / forks | :x: No |

---

## 2. Reporting a Vulnerability / Reportar una Vulnerabilidad

La seguridad de `WakeToLan4G` y la protección de los dispositivos locales es una prioridad.

Si descubres una vulnerabilidad de seguridad en la aplicación móvil o en el agente de Windows (por ejemplo, bypass de autenticación por token, vulnerabilidad en el manejo de paquetes UDP, o inyección de comandos):

1. **NO abras un Issue público en GitHub** para evitar exponer a otros usuarios.
2. Envía un correo electrónico detallado a:  
   📧 **`k3v1bvo@gmail.com`**
3. Incluye:
   - Descripción detallada del problema.
   - Pasos para reproducir o prueba de concepto (PoC).
   - Componente afectado (`app/` o `windows_agent/`).
   - Impacto estimado en la confidencialidad, integridad o disponibilidad.

Recibirás una respuesta inicial en un plazo máximo de 48 horas. Una vez corregido el problema, se publicará la solución y se otorgará el reconocimiento correspondiente si el investigador así lo desea.

---

## 3. Prácticas de Seguridad Recomendadas para el Usuario
- **Cambiar el Token por Defecto:** Nunca utilices el token por defecto (`kevin2026`) en entornos de red compartidos. Define un token robusto y único en `SettingsDialog` y en `config.json`.
- **Restricción de Puertos:** No abras el puerto del agente (`8080`) directamente hacia la Internet pública sin una capa de túnel cifrado (como WireGuard, Tailscale o SSH tunneling) o una regla estricta de firewall. El puerto 9 (UDP) es seguro para paquetes mágicos ya que solo activa la tarjeta de red mediante la coincidencia estricta de la dirección MAC física.
