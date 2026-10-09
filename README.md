<div align="center">

<h1>📱 VitalTrace Mobile</h1>

<p><strong>Continuous clinical follow-up platform · Android Application</strong></p>

<p><em>Traza el recorrido de la información clínica desde que el paciente la consulta o registra en su dispositivo móvil hasta su procesamiento por la plataforma VitalTrace.</em></p>

<p>
  <img alt="Android" src="https://img.shields.io/badge/Android-Application-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge">
  <img alt="Hilt" src="https://img.shields.io/badge/DI-Hilt-017D84?style=for-the-badge">
  <img alt="Room" src="https://img.shields.io/badge/Database-Room-4285F4?style=for-the-badge">
  <img alt="WorkManager" src="https://img.shields.io/badge/Background-WorkManager-3DDC84?style=for-the-badge">
</p>

<p>
  <img alt="Status" src="https://img.shields.io/badge/status-in_development-01305E?style=flat-square">
  <img alt="Application version" src="https://img.shields.io/badge/App-v1-017D84?style=flat-square">
  <img alt="Architecture" src="https://img.shields.io/badge/architecture-MVVM-60CEC8?style=flat-square">
  <img alt="License" src="https://img.shields.io/badge/license-academic_prototype-283137?style=flat-square">
</p>

</div>

<hr>

<blockquote>

<strong>⚠️ Aviso de alcance.</strong>

VitalTrace Mobile es un prototipo académico desarrollado por <strong>QuantumMinds</strong> para el Hackathon Nicaragua 2026.

La aplicación registra, consulta y presenta información para <strong>seguimiento clínico continuo</strong>. No interpreta resultados médicos, no realiza diagnósticos y no sustituye la valoración de un profesional de la salud.

</blockquote>

<h2>📋 Tabla de contenido</h2>

<table>

<tr>

<td>

<a href="#-sobre-el-proyecto">Sobre el proyecto</a><br>
<a href="#-funcionalidades-implementadas">Funcionalidades implementadas</a><br>
<a href="#-stack-tecnológico">Stack tecnológico</a><br>
<a href="#-arquitectura">Arquitectura</a><br>
<a href="#-estructura-de-la-aplicación">Estructura de la aplicación</a><br>

</td>

<td>

<a href="#-roles-y-permisos">Roles y permisos</a><br>
<a href="#-autenticación">Autenticación</a><br>
<a href="#-comunicación-con-la-api">Comunicación con la API</a><br>
<a href="#-instalación">Instalación</a><br>

</td>

<td>

<a href="#-convenciones-de-código">Convenciones de código</a><br>
<a href="#-despliegue">Despliegue</a><br>
<a href="#-reglas-de-negocio">Reglas de negocio</a><br>
<a href="#-equipo">Equipo</a><br>

</td>

</tr>

</table>

<hr>

<h2>🎯 Sobre el proyecto</h2>

<p>

<strong>VitalTrace Mobile</strong> es el cliente Android oficial del ecosistema VitalTrace.

La aplicación permite a pacientes y familiares autorizados consultar información clínica, registrar mediciones, revisar tratamientos, administrar citas médicas y mantenerse informados mediante notificaciones, todo desde una experiencia móvil moderna desarrollada de forma nativa para Android.

Toda la lógica clínica permanece centralizada en la <strong>VitalTrace REST API</strong>, mientras que la aplicación móvil actúa como cliente de presentación, manteniendo una arquitectura desacoplada que facilita el mantenimiento, la escalabilidad y la evolución independiente de cada componente.

</p>

<table>

<tr>

<td align="center" width="25%">

<h3>📱</h3>

<strong>Android nativo</strong><br>

<sub>Desarrollado completamente con Kotlin y Jetpack Compose.</sub>

</td>

<td align="center" width="25%">

<h3>🧩</h3>

<strong>Arquitectura MVVM</strong><br>

<sub>Separación entre Presentation, Domain y Data para facilitar el mantenimiento.</sub>

</td>

<td align="center" width="25%">

<h3>🌐</h3>

<strong>Cliente REST</strong><br>

<sub>Comunicación segura con la API mediante Retrofit.</sub>

</td>

<td align="center" width="25%">

<h3>⚡</h3>

<strong>Reactiva</strong><br>

<sub>Interfaces dinámicas utilizando StateFlow y Coroutines.</sub>

</td>

</tr>

</table>

<hr>

<h2>✨ Funcionalidades implementadas</h2>

<table>
<thead>
<tr>
<th align="left">Área</th>
<th align="left">Capacidades actuales</th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>Inicio y personalización</strong></td>
<td>Onboarding inicial, tema claro/oscuro/sistema, texto ampliado, avatar local y protección opcional contra capturas de pantalla.</td>
</tr>
<tr>
<td><strong>Autenticación</strong></td>
<td>Inicio de sesión con opción de recordar, restauración y validación de sesión, cierre de sesión, primer acceso y recuperación de contraseña.</td>
</tr>
<tr>
<td><strong>Experiencia multirrol</strong></td>
<td>Portales móviles para paciente, familiar y enfermería; acceso directo cuando existe un solo rol y selector de portal cuando el usuario posee varios.</td>
</tr>
<tr>
<td><strong>Panel del paciente</strong></td>
<td>Resumen de salud, alertas, próxima cita, medición reciente con tendencia, tratamientos activos, accesos rápidos y actualización manual.</td>
</tr>
<tr>
<td><strong>Mediciones</strong></td>
<td>Registro y detalle, paginación, búsqueda, filtros por revisión/tipo/período/riesgo, gráficos e indicadores, preferencias recordadas y recordatorios diarios configurables con posposición.</td>
</tr>
<tr>
<td><strong>Citas</strong></td>
<td>Próximas citas e historial, búsqueda, detalle, paginación, apertura en el calendario del dispositivo y recordatorios a 10, 7, 5, 3 y 1 día.</td>
</tr>
<tr>
<td><strong>Historia clínica</strong></td>
<td>Diagnósticos, tratamientos, mediciones y evoluciones clínicas; línea de tiempo con búsqueda/filtros, educación de MedlinePlus y exportación del expediente a PDF.</td>
</tr>
<tr>
<td><strong>Tratamientos y notificaciones</strong></td>
<td>Listado y detalle de tratamientos con búsqueda/filtro de activos; centro de notificaciones, conteo de no leídas y acciones para marcar una o todas como leídas.</td>
</tr>
<tr>
<td><strong>Familiares autorizados</strong></td>
<td>El paciente puede autorizar o revocar accesos. El portal familiar permite seleccionar un paciente vinculado y consultar su resumen, citas, mediciones, tratamientos e historia clínica.</td>
</tr>
<tr>
<td><strong>Portal de enfermería</strong></td>
<td>Resumen operativo, búsqueda y selección de pacientes, perfil y resumen clínico, citas, mediciones y registro de nuevas mediciones, diagnósticos, tratamientos, historia clínica y gestión de alertas.</td>
</tr>
<tr>
<td><strong>Modo offline</strong></td>
<td>Caché en memoria y Room para citas, mediciones y tratamientos, con indicadores visuales cuando se presentan datos almacenados sin conexión.</td>
</tr>
<tr>
<td><strong>Accesibilidad y calidad</strong></td>
<td>Diseño adaptable, estados de carga tipo skeleton, descripciones semánticas, texto ampliado y pruebas instrumentadas de accesibilidad.</td>
</tr>
</tbody>
</table>

<hr>

<h2>⚙️ Stack tecnológico</h2>

<table>

<thead>

<tr>

<th align="left">Capa</th>

<th align="left">Tecnología</th>

<th align="left">Detalle</th>

</tr>

</thead>

<tbody>

<tr>

<td>Lenguaje</td>

<td><strong>Kotlin 2.2.10</strong></td>

<td>Lenguaje principal de desarrollo</td>

</tr>

<tr>

<td>UI</td>

<td><strong>Jetpack Compose BOM 2026.02.01</strong></td>

<td>Interfaz declarativa nativa</td>

</tr>

<tr>

<td>Diseño</td>

<td><strong>Material Design 3</strong></td>

<td>Componentes oficiales de Android</td>

</tr>

<tr>

<td>Arquitectura</td>

<td><strong>MVVM</strong></td>

<td>Separación de responsabilidades mediante ViewModels y StateFlow</td>

</tr>

<tr>

<td>Inyección de dependencias</td>

<td><strong>Hilt 2.59.2</strong></td>

<td>Administración centralizada de dependencias</td>

</tr>

<tr>

<td>Cliente HTTP</td>

<td><strong>Retrofit 3.0.0 + OkHttp 5.1.0</strong></td>

<td>Consumo de la API REST, autenticación, logging y medición de tiempos de red</td>

</tr>

<tr>

<td>Serialización</td>

<td><strong>Kotlinx Serialization 1.9.0</strong></td>

<td>Conversión tipada entre JSON y modelos Kotlin</td>

</tr>

<tr>

<td>Procesos asíncronos</td>

<td><strong>Kotlin Coroutines 1.10.2</strong></td>

<td>Operaciones en segundo plano</td>

</tr>

<tr>

<td>Estado de la interfaz</td>

<td><strong>StateFlow</strong></td>

<td>Actualización reactiva de pantallas</td>

</tr>

<tr>

<td>Navegación</td>

<td><strong>Navigation Compose 2.9.8</strong></td>

<td>Navegación entre pantallas</td>

</tr>

<tr>

<td>Persistencia local</td>

<td><strong>Room 2.8.4 + DataStore 1.2.1</strong></td>

<td>Caché local de datos del paciente, preferencias y sesión</td>

</tr>

<tr>

<td>Trabajo en segundo plano</td>

<td><strong>WorkManager 2.11.2</strong></td>

<td>Programación confiable de recordatorios de citas y mediciones</td>

</tr>

<tr>

<td>Generación de código</td>

<td><strong>KSP 2.3.10</strong></td>

<td>Procesamiento de anotaciones para Hilt y Room</td>

</tr>

<tr>

<td>API externa</td>

<td><strong>MedlinePlus</strong></td>

<td>Consulta de información médica educativa</td>

</tr>

</tbody>

</table>

<hr>

<h2>🏛️ Arquitectura</h2>

<p>

La aplicación sigue una arquitectura <strong>MVVM</strong> por funcionalidades, con separación entre presentación, dominio y datos.

Cada pantalla interactúa con su ViewModel correspondiente. Los repositorios coordinan la API remota y, cuando aplica, el caché local de Room; Hilt proporciona las dependencias y DataStore conserva la sesión y las preferencias.

</p>

<pre>

┌─────────────────────────────────────────────────────────────┐
│                 Jetpack Compose UI                           │
└───────────────────────────────┬─────────────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │                  ViewModels                    │
        │       Estado de pantalla · StateFlow           │
        └───────────────────────┬────────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │                  Use Cases                     │
        │      Coordinación de reglas de negocio         │
        └───────────────────────┬────────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │                 Repositories                   │
        │      Abstracción del acceso a los datos        │
        └───────────────────────┬────────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │ Retrofit + OkHttp + Kotlinx Serialization     │
        │        Cliente HTTP y serialización JSON       │
        └───────────────────────┬────────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │              VitalTrace REST API               │
        │     Procesamiento de la lógica del servidor    │
        └───────────────────────────────────────────────┘

</pre>

<p>

Room mantiene snapshots locales de citas, mediciones y tratamientos para mejorar la disponibilidad de la información. WorkManager ejecuta recordatorios de citas y mediciones incluso cuando la aplicación no está en primer plano.

</p>

<h3>📦 Contrato de comunicación</h3>

<p>

La aplicación consume la API utilizando un contrato de respuesta uniforme para todos los recursos.

</p>

<table>

<thead>

<tr>

<th align="left">Campo</th>

<th align="left">Descripción</th>

</tr>

</thead>

<tbody>

<tr>

<td><code>data</code></td>

<td>Objeto o colección devuelta por el servidor.</td>

</tr>

<tr>

<td><code>message</code></td>

<td>Mensaje descriptivo del resultado de la operación.</td>

</tr>

<tr>

<td><code>errors</code></td>

<td>Errores de validación cuando la solicitud no es válida.</td>

</tr>

</tbody>

</table>

<details>

<summary><strong>Ver ejemplo de respuesta</strong></summary>

```json
{
  "data": {
    "id": 12,
    "full_name": "John Doe",
    "record_number": "VT-2026-014"
  },
  "message": "Patient retrieved successfully.",
  "errors": null
}
```

</details>

<hr>
<h2>🗂️ Estructura de la aplicación</h2>

<p>

La aplicación está organizada siguiendo una arquitectura modular basada en capas, donde cada funcionalidad mantiene una separación clara entre presentación, dominio y acceso a datos.

Cada módulo es independiente y contiene únicamente los componentes necesarios para su funcionamiento, favoreciendo la reutilización del código y facilitando el mantenimiento del proyecto.

</p>

<table>

<thead>

<tr>

<th align="left">Capa</th>

<th align="left">Responsabilidad</th>

</tr>

</thead>

<tbody>

<tr>

<td><strong>Presentation</strong></td>

<td>Pantallas Compose, navegación, ViewModels y estados de interfaz.</td>

</tr>

<tr>

<td><strong>Domain</strong></td>

<td>Casos de uso, modelos de negocio e interfaces de repositorio.</td>

</tr>

<tr>

<td><strong>Data</strong></td>

<td>Implementaciones de repositorios, Retrofit, DTOs, servicios REST y coordinación con caché local.</td>

</tr>

<tr>

<td><strong>Core</strong></td>

<td>Red, sesión, Room, DataStore, configuración global y componentes compartidos.</td>

</tr>

<tr>

<td><strong>DI</strong></td>

<td>Módulos Hilt para la inyección de dependencias.</td>

</tr>

</tbody>

</table>

<p>

Las funcionalidades conectadas a la API siguen esta estructura por capas; las pantallas puramente locales utilizan solo los paquetes necesarios.

</p>

<pre>

feature/

├── data/

│   ├── remote/

│   ├── repository/

│   └── dto/

│

├── domain/

│   ├── model/

│   ├── repository/

│   └── usecase/

│

└── presentation/

    ├── screen/

    ├── components/

    ├── state/

    └── viewmodel/

</pre>

<hr>

<h2>👥 Roles y permisos</h2>

<p>

La aplicación adapta automáticamente la interfaz según el rol autenticado.

Las autorizaciones reales siempre son verificadas por la API, por lo que la aplicación únicamente controla la navegación y la experiencia de usuario.

Los roles móviles disponibles son <code>PATIENT</code>, <code>RELATIVE</code> y <code>NURSE</code>. Si una cuenta posee más de uno, se muestra un selector para elegir el portal; con un solo rol, la navegación es directa.

</p>

<table>

<thead>

<tr>

<th align="left">Rol</th>

<th align="left">Puede</th>

<th align="left">No puede</th>

</tr>

</thead>

<tbody>

<tr>

<td><code>PATIENT</code></td>

<td>

Consultar su información clínica.<br>

Registrar mediciones.<br>

Consultar tratamientos.<br>

Consultar historial clínico.<br>

Administrar su perfil.<br>

Consultar notificaciones.

</td>

<td>

Consultar información de otros pacientes.<br>

Modificar registros clínicos.<br>

Acceder a funciones administrativas.

</td>

</tr>

<tr>

<td><code>RELATIVE</code></td>

<td>

Consultar información de pacientes autorizados.<br>

Consultar mediciones.<br>

Consultar tratamientos.<br>

Consultar citas médicas.

</td>

<td>

Registrar información clínica.<br>

Modificar tratamientos.<br>

Acceder sin autorización vigente.

</td>

</tr>

<tr>

<td><code>NURSE</code></td>

<td>

Consultar pacientes asignados y su información clínica.<br>

Registrar mediciones para un paciente.<br>

Consultar citas, diagnósticos y tratamientos.<br>

Clasificar y escalar alertas clínicas.

</td>

<td>

Administrar usuarios o configuración del sistema.<br>

Modificar tratamientos o diagnósticos.<br>

Acceder a pacientes fuera de su asignación.

</td>

</tr>

</tbody>

</table>

<hr>

<h2>🔐 Autenticación</h2>

<p>

La aplicación utiliza tokens de acceso emitidos por la API de VitalTrace.

OkHttp incorpora el token en las solicitudes protegidas mediante el encabezado <code>Authorization: Bearer &lt;token&gt;</code>. Al iniciar la aplicación, la sesión guardada se valida contra la API antes de abrir un portal protegido.

</p>

<table>

<tr>

<td align="center"><strong>1</strong></td>

<td><code>POST /api/v1/auth/login</code></td>

<td>Valida las credenciales y devuelve el token junto con el usuario.</td>

</tr>

<tr>

<td align="center"><strong>2</strong></td>

<td><code>GET /api/v1/auth/me</code></td>

<td>Valida el token almacenado y obtiene el usuario autenticado.</td>

</tr>

<tr>

<td align="center"><strong>3</strong></td>

<td><code>POST /api/v1/auth/logout</code></td>

<td>Invalida la sesión remota y limpia el estado local.</td>

</tr>

</table>

<h3>🎫 Primer acceso</h3>

<p>

Los pacientes reciben un código de activación enviado por correo electrónico.

La aplicación implementa un flujo específico para permitir el primer acceso sin intervención del personal médico.

</p>

<ul>

<li>✅ Verificación mediante código temporal y token de activación.</li>

<li>✅ Código válido durante 24 horas.</li>

<li>✅ Creación obligatoria de contraseña.</li>

<li>✅ Inicio de sesión posterior utilizando credenciales personales.</li>

<li>🚫 El código no puede reutilizarse.</li>

</ul>

<h3>🔒 Gestión de sesión</h3>

<p>

Una vez autenticado el usuario, la aplicación mantiene el token en memoria y solo lo conserva en DataStore cuando está activada la opción <strong>Recordarme</strong>. Al cerrar sesión se eliminan el token, el caché en memoria y los snapshots de Room.

</p>

<table>

<thead>

<tr>

<th>Elemento</th>

<th>Persistencia</th>

</tr>

</thead>

<tbody>

<tr>

<td>Token de acceso</td>

<td>Memoria y, si <strong>Recordarme</strong> está activo, DataStore</td>

</tr>

<tr>

<td>Usuario y roles activos</td>

<td>StateFlow en memoria</td>

</tr>

<tr>

<td>Caché clínico</td>

<td>Memoria + Room</td>

</tr>

</tbody>

</table>

<hr>

<h2>🌐 Comunicación con la API</h2>

<p>

Las operaciones clínicas remotas son procesadas por la API REST de VitalTrace.

La aplicación nunca accede directamente a la base de datos del servidor ni implementa reglas clínicas propias. Room se utiliza exclusivamente como caché local y DataStore para sesión y preferencias.

</p>

<details open>

<summary><strong>🔓 Autenticación</strong></summary>

| Método | Endpoint | Descripción |
|---------|----------|-------------|
| POST | `/auth/login` | Inicio de sesión |
| POST | `/auth/logout` | Cerrar sesión |
| GET | `/auth/me` | Usuario autenticado |
| POST | `/auth/activation/verify-code` | Verificación del código de activación |
| POST | `/auth/activation/resend-code` | Reenvío del código |
| POST | `/auth/activation/set-password` | Creación de la contraseña inicial |
| POST | `/auth/forgot-password` | Recuperación de contraseña |
| POST | `/auth/reset-password` | Restablecimiento |

</details>

<details>

<summary><strong>👤 Paciente</strong></summary>

| Recurso | Descripción |
|----------|-------------|
| Profile | Información personal |
| Measurements | Registro, consulta, filtros y paginación de mediciones |
| Treatments | Consulta, búsqueda, detalle y paginación de tratamientos |
| Clinical History | Historial clínico, línea de tiempo y exportación PDF |
| Appointments | Consulta, búsqueda, paginación, calendario y recordatorios |
| Relatives | Consulta, autorización y revocación de familiares |
| Notifications | Centro de notificaciones y control de lectura |

</details>

<details>

<summary><strong>👨‍👩‍👦 Familiares</strong></summary>

| Recurso | Descripción |
|----------|-------------|
| Authorized Patients | Pacientes autorizados |
| Measurements | Consulta de mediciones |
| Treatments | Consulta de tratamientos |
| Appointments | Consulta de citas |
| Clinical History | Consulta del historial clínico permitido |

</details>

<details>

<summary><strong>🩺 Enfermería</strong></summary>

| Recurso | Descripción |
|----------|-------------|
| Summary | Resumen operativo y actividad pendiente |
| Patients | Búsqueda, selección, perfil y resumen clínico |
| Measurements | Consulta y registro de mediciones por paciente |
| Appointments | Citas generales y por paciente |
| Clinical Data | Diagnósticos, tratamientos e historia clínica |
| Alerts | Consulta, filtros, clasificación y escalamiento |

</details>

<details>

<summary><strong>📚 MedlinePlus</strong></summary>

<p>

La aplicación integra MedlinePlus como fuente de información médica educativa.

Esta integración es consumida directamente desde Android mediante Retrofit independiente, sin pasar por la API principal de VitalTrace.

</p>

</details>

<hr>
<hr>

<h2>🚀 Instalación</h2>

<p>

Siga los pasos descritos a continuación para ejecutar la aplicación Android en un entorno de desarrollo local.

</p>

<h3>📋 Requisitos previos</h3>

<table>

<thead>

<tr>

<th align="left">Requisito</th>

<th align="left">Versión recomendada</th>

</tr>

</thead>

<tbody>

<tr>

<td>Android Studio</td>

<td>Versión compatible con Android Gradle Plugin 9.2.1</td>

</tr>

<tr>

<td>JDK</td>

<td>17</td>

</tr>

<tr>

<td>Android SDK</td>

<td>API 36 (compileSdk 36.1, targetSdk 36 y minSdk 26)</td>

</tr>

<tr>

<td>Gradle</td>

<td>9.4.1, incluido mediante Gradle Wrapper</td>

</tr>

<tr>

<td>Git</td>

<td>Última versión estable</td>

</tr>

</tbody>

</table>

<hr>

<h3>📥 Clonar el repositorio</h3>

```bash
git clone https://github.com/QuantumMinds/VitalTracea_App.git

cd VitalTracea_App
```

<hr>

<h3>⚙️ Configurar la API</h3>

<p>

Las URL de la API se definen por tipo de compilación en <code>app/build.gradle.kts</code> y se exponen a la aplicación mediante <code>BuildConfig.BASE_URL</code>.

</p>

```kotlin
buildTypes {
    debug {
        buildConfigField("String", "BASE_URL", "\"https://api.vitaltrace.lat/api/v1/\"")
    }

    create("local") {
        initWith(getByName("debug"))
        buildConfigField("String", "BASE_URL", "\"http://TU_IP:8000/api/v1/\"")
    }

    release {
        buildConfigField("String", "BASE_URL", "\"https://api.vitaltrace.lat/api/v1/\"")
    }
}
```

<p>

Para utilizar el backend local, sustituya <code>TU_IP</code> por la IP del servidor dentro de la misma red y ejecute la variante <strong>local</strong>.

Las variantes <strong>debug</strong> y <strong>release</strong> consumen la API oficial por defecto.

</p>

<hr>

<h3>▶️ Ejecutar la aplicación</h3>

```bash
./gradlew assembleDebug
```

Para compilar la variante conectada al backend local:

```bash
./gradlew assembleLocal
```

También puede ejecutar la aplicación desde Android Studio seleccionando la variante <strong>debug</strong>, <strong>local</strong> o <strong>release</strong>.

Para verificar el proyecto:

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew connectedDebugAndroidTest
```

El último comando requiere un emulador o dispositivo conectado y ejecuta, entre otras, las pruebas instrumentadas de accesibilidad.

<hr>

<h2>📂 Organización del proyecto</h2>

<p>

La estructura del proyecto mantiene una separación por responsabilidades para facilitar el mantenimiento y la escalabilidad del código.

</p>

<pre>

app/src/main/java/com/vitaltrace/app/

├── core/
│   ├── cache/          # Room y caché de snapshots
│   ├── datastore/      # Persistencia de sesión
│   ├── di/             # Módulos de Hilt
│   ├── network/        # Retrofit, OkHttp e interceptores
│   ├── session/        # Estado de autenticación
│   └── settings/       # Preferencias de la aplicación
├── feature/                # Funcionalidades por dominio
├── navigation/             # Rutas y NavHost
├── ui/theme/              # Tema Material 3
├── MainActivity.kt
├── VitalTraceApp.kt
└── VitalTraceApplication.kt

</pre>

<hr>

<h2>📦 Organización por funcionalidades</h2>

<p>

Cada funcionalidad utiliza las capas que necesita. Los módulos conectados a la API separan datos, dominio y presentación; las capacidades propias del dispositivo agregan paquetes especializados como <code>reminders</code> o <code>export</code>.

</p>

<pre>

feature/

├── data/

│   ├── dto/

│   ├── remote/

│   └── repository/

│

├── domain/

│   ├── model/

│   ├── repository/

│   └── usecase/

│

└── presentation/

    ├── components/

    ├── navigation/

    ├── screen/

    ├── state/

    └── viewmodel/

# Paquetes opcionales según la funcionalidad:
├── reminders/     # WorkManager y notificaciones locales
└── export/        # Generación de documentos PDF

</pre>

<p>

Los módulos principales son <code>auth</code>, <code>home</code>, <code>appointments</code>, <code>measurements</code>, <code>treatments</code>, <code>clinicalhistory</code>, <code>timeline</code>, <code>notifications</code>, <code>profile</code>, <code>relatives</code>, <code>relativeportal</code>, <code>nurseportal</code>, <code>medlineplus</code>, <code>onboarding</code> y <code>portalselector</code>.

</p>

<hr>

<h2>📐 Convenciones de código</h2>

<p>

Con el objetivo de mantener consistencia durante el desarrollo, el proyecto adopta las siguientes convenciones.

</p>

<table>

<thead>

<tr>

<th>Elemento</th>

<th>Convención</th>

</tr>

</thead>

<tbody>

<tr>

<td>Idioma del código</td>

<td>Inglés</td>

</tr>

<tr>

<td>Arquitectura</td>

<td>MVVM</td>

</tr>

<tr>

<td>UI</td>

<td>Jetpack Compose</td>

</tr>

<tr>

<td>Inyección de dependencias</td>

<td>Hilt</td>

</tr>

<tr>

<td>Navegación</td>

<td>Navigation Compose</td>

</tr>

<tr>

<td>Estado</td>

<td>StateFlow</td>

</tr>

<tr>

<td>Programación asíncrona</td>

<td>Coroutines</td>

</tr>

<tr>

<td>Cliente HTTP</td>

<td>Retrofit</td>

</tr>

<tr>

<td>Serialización</td>

<td>Kotlinx Serialization</td>

</tr>

</tbody>

</table>

<hr>

<h2>🧩 Dependencias principales</h2>

<table>

<thead>

<tr>

<th>Biblioteca</th>

<th>Propósito</th>

</tr>

</thead>

<tbody>

<tr>

<td>Jetpack Compose</td>

<td>Construcción de interfaces de usuario.</td>

</tr>

<tr>

<td>Material Design 3</td>

<td>Componentes visuales.</td>

</tr>

<tr>

<td>Navigation Compose</td>

<td>Navegación entre pantallas.</td>

</tr>

<tr>

<td>Lifecycle</td>

<td>Ciclo de vida de ViewModels.</td>

</tr>

<tr>

<td>Hilt</td>

<td>Inyección de dependencias.</td>

</tr>

<tr>

<td>Retrofit</td>

<td>Comunicación HTTP.</td>

</tr>

<tr>

<td>Kotlinx Serialization</td>

<td>Serialización JSON tipada e integrada con Kotlin.</td>

</tr>

<tr>

<td>OkHttp</td>

<td>Interceptores y manejo de peticiones.</td>

</tr>

<tr>

<td>Room</td>

<td>Caché persistente de citas, mediciones y tratamientos.</td>

</tr>

<tr>

<td>DataStore</td>

<td>Persistencia de sesión, preferencias y configuración.</td>

</tr>

<tr>

<td>WorkManager</td>

<td>Recordatorios de citas y mediciones en segundo plano.</td>

</tr>

<tr>

<td>Kotlin Coroutines y StateFlow</td>

<td>Concurrencia estructurada y estado reactivo de la interfaz.</td>

</tr>

<tr>

<td>KSP</td>

<td>Generación de código para Room y Hilt.</td>

</tr>

</tbody>

</table>

<hr>

<h2>⚠️ Manejo de errores</h2>

<p>

La aplicación centraliza el tratamiento de errores provenientes de la API para mantener una experiencia consistente en todas las pantallas.

</p>

<table>

<thead>

<tr>

<th>Código HTTP</th>

<th>Acción</th>

</tr>

</thead>

<tbody>

<tr>

<td>200</td>

<td>Procesar respuesta.</td>

</tr>

<tr>

<td>400</td>

<td>Mostrar errores de validación.</td>

</tr>

<tr>

<td>401</td>

<td>Redirigir al inicio de sesión.</td>

</tr>

<tr>

<td>403</td>

<td>Mostrar acceso denegado.</td>

</tr>

<tr>

<td>404</td>

<td>Mostrar recurso no encontrado.</td>

</tr>

<tr>

<td>500</td>

<td>Mostrar error interno del servidor.</td>

</tr>

</tbody>

</table>

<hr>

<h2>🛡️ Buenas prácticas implementadas</h2>

<ul>

<li>Separación estricta entre Presentation, Domain y Data.</li>

<li>Una única fuente de verdad para el estado de cada pantalla.</li>

<li>ViewModels independientes por funcionalidad.</li>

<li>Inyección de dependencias mediante Hilt.</li>

<li>Consumo centralizado de servicios REST.</li>

<li>Reutilización de componentes Compose.</li>

<li>Navegación desacoplada.</li>

<li>Persistencia de sesión utilizando DataStore.</li>

<li>Caché persistente de datos del paciente mediante Room.</li>

<li>Recordatorios de citas y mediciones programados con WorkManager.</li>

<li>Serialización JSON tipada con Kotlinx Serialization.</li>

<li>Manejo uniforme de respuestas de la API.</li>

<li>Componentes reutilizables siguiendo Material Design 3.</li>

</ul>

<hr>
<h2>☁️ Despliegue</h2>

<p>

La aplicación dispone de variantes de compilación para trabajar contra el backend local o la API desplegada.

No es necesario realizar cambios en la lógica de negocio ni en la arquitectura del proyecto.

</p>

<table>

<thead>

<tr>

<th align="left">Entorno</th>

<th align="left">Descripción</th>

</tr>

</thead>

<tbody>

<tr>

<td>Desarrollo</td>

<td><strong>local</strong>: servidor de VitalTrace dentro de la misma red, con sufijo de aplicación <code>.local</code>.</td>

</tr>

<tr>

<td>Producción</td>

<td><strong>debug</strong> y <strong>release</strong>: API oficial desplegada en <code>api.vitaltrace.lat</code>.</td>

</tr>

</tbody>

</table>

<p>

La compilación para producción utiliza <strong>release</strong>. Para desarrollo con servicios locales se utiliza <strong>local</strong>; <strong>debug</strong> permite depurar contra la API oficial e incorpora logging y medición de tiempos de red.

</p>

<h3>Firma y paquete de producción</h3>

<p>La configuración de firma nunca guarda credenciales en Git. Antes de generar el paquete final, define estas variables de entorno:</p>

<ul>
<li><code>VITALTRACE_KEYSTORE_PATH</code>: ruta absoluta del keystore.</li>
<li><code>VITALTRACE_KEYSTORE_PASSWORD</code>: contraseña del keystore.</li>
<li><code>VITALTRACE_KEY_ALIAS</code>: alias de la clave.</li>
<li><code>VITALTRACE_KEY_PASSWORD</code>: contraseña de la clave.</li>
</ul>

<p>Verifica calidad y genera el Android App Bundle firmado con:</p>

<pre>
.\gradlew.bat :app:testDebugUnitTest :app:lintRelease :app:bundleRelease
</pre>

<p>El paquete publicable queda en <code>app/build/outputs/bundle/release/</code>. Si falta alguna variable, Gradle permite compilar localmente un artefacto release sin firmar, pero ese archivo no debe distribuirse.</p>

<hr>

<h2>📖 Reglas de negocio</h2>

<p>

La aplicación implementa únicamente las reglas correspondientes a la experiencia de usuario. Las validaciones críticas continúan siendo responsabilidad exclusiva de la API.

</p>

<details open>

<summary><strong>👤 Pacientes</strong></summary>

<ul>

<li>Consultar información personal.</li>

<li>Registrar mediciones de salud.</li>

<li>Consultar historial clínico.</li>

<li>Visualizar tratamientos activos.</li>

<li>Consultar citas médicas.</li>

<li>Añadir citas al calendario y recibir recordatorios locales.</li>

<li>Consultar su perfil y personalizar avatar, apariencia y accesibilidad.</li>

<li>Consultar y administrar notificaciones.</li>

<li>Configurar apariencia, accesibilidad, privacidad y recordatorios.</li>

<li>Autorizar o revocar el acceso de familiares.</li>

<li>Exportar su expediente clínico a PDF.</li>

<li>Recuperar contraseña.</li>

</ul>

</details>

<details>

<summary><strong>👨‍👩‍👧 Familiares</strong></summary>

<ul>

<li>Consultar únicamente pacientes autorizados.</li>

<li>Consultar tratamientos.</li>

<li>Consultar mediciones.</li>

<li>Consultar historial clínico permitido.</li>

<li>Consultar próximas citas.</li>

<li>Cambiar entre pacientes vinculados con autorización vigente.</li>

</ul>

</details>

<details>

<summary><strong>🩺 Enfermería</strong></summary>

<ul>

<li>Consultar y buscar pacientes asignados.</li>

<li>Revisar perfiles, resúmenes e historia clínica.</li>

<li>Registrar mediciones y consultar su evolución.</li>

<li>Consultar citas, diagnósticos y tratamientos.</li>

<li>Filtrar, clasificar y escalar alertas.</li>

</ul>

</details>

<details>

<summary><strong>🔐 Seguridad</strong></summary>

<ul>

<li>Las pantallas protegidas requieren autenticación.</li>

<li>La sesión se valida antes de acceder a recursos protegidos.</li>

<li>La aplicación elimina el token y el caché clínico al cerrar sesión.</li>

<li>Los permisos dependen del rol autenticado.</li>

<li>La autorización final siempre es validada por la API.</li>

</ul>

</details>

<hr>

<hr>

<h2>🧪 Calidad del software</h2>

<p>

Durante el desarrollo del proyecto se aplicaron prácticas orientadas a mantener una base de código mantenible, escalable y consistente.

</p>

<ul>

<li>Arquitectura MVVM.</li>

<li>Separación por capas.</li>

<li>Repositorios desacoplados.</li>

<li>Inyección de dependencias mediante Hilt.</li>

<li>Estado reactivo utilizando StateFlow.</li>

<li>Componentes reutilizables con Jetpack Compose.</li>

<li>Navegación centralizada.</li>

<li>Consumo uniforme de servicios REST.</li>

<li>Manejo centralizado de errores.</li>

<li>Persistencia opcional del token de sesión mediante DataStore.</li>

<li>Caché local estructurado mediante Room.</li>

<li>Tareas confiables en segundo plano mediante WorkManager.</li>

<li>Pruebas unitarias para sesión, enrutamiento por roles, ViewModels, mapeos, filtros y planificación de recordatorios.</li>

<li>Pruebas instrumentadas de accesibilidad para acciones principales y contenido clínico.</li>

</ul>

<hr>

<h2>👨‍💻 Equipo de desarrollo</h2>

<table>

<thead>

<tr>

<th>Proyecto</th>

<th>VitalTrace Mobile</th>

</tr>

</thead>

<tbody>

<tr>

<td>Institución</td>

<td>Universidad Americana (UAM)</td>

</tr>

<tr>

<td>Evento</td>

<td>Hackathon Nicaragua 2026</td>

</tr>

<tr>

<td>Equipo</td>

<td>QuantumMinds</td>

</tr>

<tr>

<td>Aplicación</td>

<td>Cliente Android oficial de VitalTrace</td>

</tr>

<tr>

<td>Arquitectura</td>

<td>MVVM + Clean Architecture</td>

</tr>

<tr>

<td>Lenguaje</td>

<td>Kotlin</td>

</tr>

<tr>

<td>Interfaz</td>

<td>Jetpack Compose</td>

</tr>

<tr>

<td>Backend</td>

<td>Laravel REST API</td>

</tr>

</tbody>

</table>

<hr>

<div align="center">

<h3>VitalTrace Mobile</h3>

<p>

Aplicación Android desarrollada como parte del ecosistema VitalTrace.

Construida con Kotlin, Jetpack Compose y Material Design 3 para proporcionar una experiencia moderna, segura y escalable para el seguimiento clínico continuo.

</p>

<p>

<strong>Hackathon Nicaragua 2026</strong><br>
<strong>QuantumMinds</strong>

</p>

</div>
