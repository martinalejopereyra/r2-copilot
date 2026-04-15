## 🛠 R2 API - Process a Merchant
Este endpoint permite actualizar o procesar la información de un comercio (merchant) existente utilizando su identificador externo.
## 🔌 Detalle del Endpoint

* Método: PATCH
* URL (Dev): https://r2capital.co{external_id}

## Parámetros de Ruta (Path Params)

| Parámetro | Tipo | Requerido | Descripción |
|---|---|---|---|
| external_id | string | Sí | El identificador único del comercio en tu plataforma. |

## Parámetros de Consulta (Query Params)

| Parámetro | Tipo | Requerido | Descripción |
|---|---|---|---|
| api_key | string | Sí | Tu clave de API para autenticar la petición. |

------------------------------
## 📦 Estructura del Body (JSON)
La solicitud acepta un objeto con los siguientes campos opcionales para actualizar la información del comercio:

* additional_information: Objeto con metadatos adicionales.
* address: Detalles de la ubicación física del negocio.
* business: Información legal y comercial de la entidad.
* contact: Datos de contacto (email, teléfono, etc.).
* banks: Arreglo de objetos con la información bancaria para dispersiones.
* status: Estado actual del comercio en el flujo.

------------------------------
## 🚦 Respuestas (Responses)

| Código | Descripción |
|---|---|
| 200 OK | El comercio fue procesado o actualizado exitosamente. |
| 400 Bad Request | Error en la validación de los datos enviados. |
| 404 Not Found | No se encontró un comercio con el external_id proporcionado. |
| 500 Internal Server Error | Error inesperado en el servidor de R2. |

------------------------------

Nota: Esta documentación corresponde a la versión v1.2.1 de la API.

¿Te gustaría que genere un ejemplo de código en Python o Node.js para realizar esta petición PATCH?

