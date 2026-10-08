# Contribuir a VitalTrace Mobile

Este repositorio usa un flujo basado en Pull Requests, ramas de corta duración y Conventional Commits.

## Ramas permanentes

- `main`: código estable y apto para una versión.
- `develop`: integración de cambios aprobados para la siguiente versión.

No se debe trabajar ni realizar `push` directamente sobre estas ramas. Todo cambio debe ingresar mediante un Pull Request.

## Ramas de trabajo

Crea cada rama desde `develop` y utiliza uno de estos formatos:

- `feature/<ticket>-descripcion`
- `fix/<ticket>-descripcion`
- `docs/<ticket>-descripcion`
- `refactor/<ticket>-descripcion`
- `release/<version>`
- `hotfix/<ticket>-descripcion`, creada desde `main` únicamente para incidentes urgentes de producción.

Usa nombres en minúsculas, palabras separadas por guiones y el identificador del ticket cuando exista.

## Conventional Commits

Los mensajes deben cumplir esta estructura:

```text
tipo(alcance opcional): descripción breve en imperativo
```

Tipos admitidos: `feat`, `fix`, `docs`, `test`, `refactor`, `perf`, `build`, `ci`, `chore` y `revert`.

Ejemplos:

```text
feat(auth): add first-access verification
fix(measurements): prevent duplicate submissions
docs: document the release workflow
```

Usa `!` y una sección `BREAKING CHANGE:` cuando el cambio rompa compatibilidad.

## Pull Requests

1. Actualiza tu rama con `develop`.
2. Ejecuta las pruebas y verificaciones locales.
3. Abre el Pull Request hacia `develop`; los releases y hotfixes son las únicas excepciones.
4. Completa toda la plantilla y enlaza el ticket correspondiente.
5. Espera la aprobación y la finalización de los controles automáticos.
6. Resuelve las conversaciones pendientes antes de integrar.
7. Utiliza **Squash and merge** para que el título validado del Pull Request sea el commit final.
8. Elimina la rama temporal después de integrar.

## Verificación local

En Windows:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
```

En Linux o macOS:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

## Versiones

VitalTrace usa versionado semántico. Las etiquetas tienen el formato `vMAJOR.MINOR.PATCH` y se crean únicamente desde un commit estable de `main` después de aprobar el release.

## Seguridad

No incluyas claves de firma, tokens, contraseñas, archivos `.jks`, `.keystore` ni archivos locales con credenciales. Si un secreto llega al historial, debe revocarse y rotarse; eliminarlo solamente del último commit no es suficiente.

