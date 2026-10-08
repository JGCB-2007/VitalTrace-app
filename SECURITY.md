# Política de seguridad

## Reportar una vulnerabilidad

No publiques credenciales, datos clínicos ni detalles explotables en un issue público. Comunica el hallazgo de forma privada al responsable del repositorio y proporciona los pasos mínimos para reproducirlo.

## Archivos sensibles

Las claves de firma y sus contraseñas se mantienen fuera de Git. En automatizaciones se almacenan como secretos cifrados del repositorio o del entorno, nunca como archivos versionados.

Si una credencial se expone, se debe revocar o rotar inmediatamente y revisar el historial antes de continuar con una publicación.

