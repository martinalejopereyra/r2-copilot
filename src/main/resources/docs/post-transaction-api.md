## 💸 R2 API - Add Transactions
Este endpoint permite registrar una o más transacciones financieras dentro de la plataforma de R2.
## 🚀 Detalle del Endpoint

* Método: POST
* URL (Dev): https://r2capital.co

[!IMPORTANT]
Solo se permite enviar transacciones de un único financiamiento por cada petición.

## 📦 Parámetros del Body (JSON)
La solicitud requiere un objeto que contenga la información de las transacciones a procesar:

* Transaction data: Objeto principal que encapsula los datos de la operación.
* ADD object: Estructura para añadir los detalles específicos de cada transacción.

## 🚦 Respuestas de la API

| Código | Descripción |
|---|---|
| 201 Created | La(s) transacción(es) fueron creadas exitosamente. |
| 400 Bad Request | Error en el formato o validación de los datos enviados. |
| 404 Not Found | El recurso o financiamiento especificado no existe. |
| 500 Internal Server Error | Error inesperado en el servidor de R2. |

------------------------------
## 💻 Ejemplo de Uso (cURL)

curl --request POST \
--url https://r2capital.co \
--header 'accept: application/json' \
--header 'content-type: application/json'

------------------------------

Nota: Documentación basada en la versión v1.2.1 de R2 API Docs.

¿Te gustaría que te ayude con los ejemplos de los objetos de Transaction data para completar el cuerpo del mensaje?

