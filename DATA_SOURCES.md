# 🌐 External Data Sources & Services

This document details all external APIs, network services, and data flows utilized by `WakeToLan4G`.

---

## 1. External Network Services

### DuckDNS (Dynamic DNS Service)
- **Proveedor:** DuckDNS ([https://www.duckdns.org/](https://www.duckdns.org/))
- **Finalidad:** Resolución de nombres de dominio dinámicos para permitir el envío de paquetes mágicos Wake-on-LAN y comandos de control a través de conexiones móviles (4G / Internet WAN) sin requerir una IP pública fija.
- **Protocolo:** HTTPS GET Request para actualización de IP; resolución DNS UDP estándar (puerto 53).
- **Términos de Servicio:** Cumple con la política de uso no comercial y libre de DuckDNS.
- **Datos Transmitidos:** Dirección IP pública del router doméstico y token secreto de actualización. No se transmiten datos personales, nombres, identificadores de dispositivo ni contraseñas.

---

## 2. Modelos de IA o Datasets Externos
- **Estado:** Ninguno. `WakeToLan4G` no incluye datasets de entrenamiento, modelos de lenguaje ni componentes de inteligencia artificial de terceros embebidos.
