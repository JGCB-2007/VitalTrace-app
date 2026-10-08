# Protección de ramas en GitHub

El repositorio usa el ruleset activo **Protect main and develop**, dirigido a la rama predeterminada (`main`) y al patrón `develop`.

## Reglas aplicadas

- Bloquear eliminaciones y actualizaciones forzadas.
- Exigir Pull Request antes de integrar.
- Exigir al menos una aprobación.
- Descartar aprobaciones antiguas cuando se agreguen commits.
- Exigir revisión de CODEOWNERS.
- Exigir que todas las conversaciones estén resueltas.
- Exigir que la rama esté actualizada antes de integrar.
- Restringir el método de integración a **Squash and merge**.
- Permitir bypass únicamente al rol administrador como mecanismo de recuperación.

## Controles requeridos

Después de que los workflows se integren y se ejecuten por primera vez, agrega estos controles al ruleset:

- `Test, lint and build`
- `Validate PR title and commits`
- `Reject sensitive files`

GitHub solo permite seleccionar controles que ya hayan sido reportados al repositorio. Sus nombres deben coincidir exactamente con los jobs definidos en `.github/workflows`.
