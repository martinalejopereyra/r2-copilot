## 🚀 R2 Embedded Experience - Complete Integration Guide
Este documento proporciona una visión 360° de la plataforma R2, consolidando toda la documentación técnica necesaria para una implementación exitosa de servicios financieros embebidos.
## 📋 Tabla de Contenidos

1. Visión General (Overview)
2. Generación de Tokens JWT
3. Inicialización de Componentes
4. Integración Móvil
5. Seguridad y CSP
6. Historial de Cambios (Changelog)

------------------------------
## 📖 Visión General (Overview)
R2 permite integrar flujos de crédito y tarjetas directamente en tu ecosistema.

* Acceso: Solicita tus credenciales a [email protected].
* Credenciales: Recibirás un keyId y un JWTSecret para los entornos de Desarrollo y Producción.
* Flujo: Servidor (Genera JWT) → Cliente (Carga SDK) → Componente R2 (Se inicializa con JWT). [1, 2]

## 🔑 Generación de Tokens JWT
La autenticación se basa en HS256. El token debe generarse en tu backend para no exponer secretos. [1, 3]
Ejemplo de Payload (Python):

payload = {
"exp": datetime.now(timezone.utc) + timedelta(seconds=600),
"mid": "ID_DEL_COMERCIO",    # ID del cliente
"country": "CO"              # Código de país
}token = jwt.encode(payload, JWT_SECRET, algorithm='HS256', headers={"kid": JWT_KID})


* Claims requeridos: exp (expiración), mid (Merchant ID) y country. [1]

## 🛠 Inicialización de Componentes
Una vez generado el token, se debe incluir el script de R2 e inicializar el componente específico (ej. Solicitud de Crédito). [2, 4]

1. Importar Script: Carga la librería de R2 en tu frontend.
2. Renderizar: Usa el método de inicialización pasando el token generado.

## 📱 Integración Móvil
Para apps nativas, utiliza WebViews con acceso a hardware esencial para el proceso de KYC: [2]

* Cámara: Para fotos de documentos e identidad.
* Archivos: Para subir comprobantes existentes.
* Ubicación: Requerido para validaciones de seguridad geográfica.

## 🛡 Seguridad y CSP
Para evitar bloqueos de carga (errores de CORS/recursos), tu Content Security Policy debe permitir los siguientes dominios: [5]

* https://*.r2.co (Core de R2)
* https://docucdn-a.akamaihd.net y https://*.docusign.net (Para lectura y firma de contratos).
* https://*.amplitude.com y https://*.customer.io (Para analíticas de flujo).

## 🔄 Historial de Cambios (Changelog)

* v1.2.1: Versión actual de la documentación.
* v2.0 (Hito): Eliminación de endpoints antiguos de transacciones y colecciones; inclusión de nuevos callbacks para eventos de seguros y financiamiento. [2, 6]

------------------------------

Estado del Documento: Basado en la documentación oficial v1.2.1.
Contacto de Soporte: [email protected]


[1] [https://r2-api-docs.readme.io](https://r2-api-docs.readme.io/docs/jwt-token-generation)
[2] [https://www.aluracursos.com](https://www.aluracursos.com/blog/como-escribir-un-readme-increible-en-tu-github)
[3] [https://developers.cloudflare.com](https://developers.cloudflare.com/r2/llms-full.txt)
[4] [https://developers.cloudflare.com](https://developers.cloudflare.com/r2/api/tokens/)
[5] [https://r2-api-docs.readme.io](https://r2-api-docs.readme.io/docs/content-security-policy-csp)
[6] [https://r2-api-docs.readme.io](https://r2-api-docs.readme.io/changelog)
