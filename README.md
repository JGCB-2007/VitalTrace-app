<div align="center">

<h1>📱 VitalTrace Mobile</h1>

<p><strong>Continuous clinical follow-up platform · Android Application</strong></p>

<p><em>Traza el recorrido de la información clínica desde que el paciente la consulta o registra en su dispositivo móvil hasta su procesamiento por la plataforma VitalTrace.</em></p>

<p>
  <img alt="Android" src="https://img.shields.io/badge/Android-Application-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.2-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge">
  <img alt="Hilt" src="https://img.shields.io/badge/DI-Hilt-017D84?style=for-the-badge">
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

<td><strong>Kotlin 2.2</strong></td>

<td>Lenguaje principal de desarrollo</td>

</tr>

<tr>

<td>UI</td>

<td><strong>Jetpack Compose</strong></td>

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

<td><strong>Hilt</strong></td>

<td>Administración centralizada de dependencias</td>

</tr>

<tr>

<td>Cliente HTTP</td>

<td><strong>Retrofit</strong></td>

<td>Comunicación con la API REST</td>

</tr>

<tr>

<td>Serialización</td>

<td><strong>Gson</strong></td>

<td>Conversión entre JSON y objetos Kotlin</td>

</tr>

<tr>

<td>Procesos asíncronos</td>

<td><strong>Kotlin Coroutines</strong></td>

<td>Operaciones en segundo plano</td>

</tr>

<tr>

<td>Estado de la interfaz</td>

<td><strong>StateFlow</strong></td>

<td>Actualización reactiva de pantallas</td>

</tr>

<tr>

<td>Navegación</td>

<td><strong>Navigation Compose</strong></td>

<td>Navegación entre pantallas</td>

</tr>

<tr>

<td>Persistencia local</td>

<td><strong>DataStore</strong></td>

<td>Almacenamiento seguro de preferencias y sesión</td>

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

La aplicación sigue una arquitectura <strong>MVVM</strong> con una separación estricta entre presentación, lógica de negocio y acceso a datos.

Cada pantalla interactúa únicamente con su ViewModel correspondiente, mientras que la comunicación con la API se realiza mediante Repositories especializados y Retrofit.

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
        │              Retrofit + Gson                   │
        │        Cliente HTTP y serialización JSON       │
        └───────────────────────┬────────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │              VitalTrace REST API               │
        │     Procesamiento de la lógica del servidor    │
        └───────────────────────────────────────────────┘

</pre>

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

<td>Implementaciones de repositorios, Retrofit, DTOs y servicios REST.</td>

</tr>

<tr>

<td><strong>Core</strong></td>

<td>Configuraciones globales, utilidades, constantes y componentes compartidos.</td>

</tr>

<tr>

<td><strong>DI</strong></td>

<td>Módulos Hilt para la inyección de dependencias.</td>

</tr>

</tbody>

</table>

<p>

Cada funcionalidad del sistema implementa la misma estructura interna para mantener consistencia durante el desarrollo.

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

</tbody>

</table>

<hr>

<h2>🔐 Autenticación</h2>

<p>

La aplicación utiliza autenticación basada en sesiones mediante Laravel Sanctum.

Todas las solicitudes protegidas requieren una sesión válida establecida previamente por la API.

</p>

<table>

<tr>

<td align="center"><strong>1</strong></td>

<td><code>GET /sanctum/csrf-cookie</code></td>

<td>Obtiene la cookie CSRF requerida por Sanctum.</td>

</tr>

<tr>

<td align="center"><strong>2</strong></td>

<td><code>POST /api/v1/auth/login</code></td>

<td>Autentica al usuario.</td>

</tr>

<tr>

<td align="center"><strong>3</strong></td>

<td><code>GET /api/v1/auth/me</code></td>

<td>Obtiene el usuario autenticado.</td>

</tr>

</table>

<h3>🎫 Primer acceso</h3>

<p>

Los pacientes reciben un código de activación enviado por correo electrónico.

La aplicación implementa un flujo específico para permitir el primer acceso sin intervención del personal médico.

</p>

<ul>

<li>✅ Activación mediante código temporal.</li>

<li>✅ Código válido durante 24 horas.</li>

<li>✅ Creación obligatoria de contraseña.</li>

<li>✅ Inicio de sesión posterior utilizando credenciales personales.</li>

<li>🚫 El código no puede reutilizarse.</li>

</ul>

<h3>🔒 Gestión de sesión</h3>

<p>

Una vez autenticado el usuario, la aplicación almacena únicamente la información necesaria para mantener la sesión activa utilizando DataStore.

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

<td>Usuario autenticado</td>

<td>DataStore</td>

</tr>

<tr>

<td>Rol</td>

<td>DataStore</td>

</tr>

<tr>

<td>Estado de autenticación</td>

<td>DataStore</td>

</tr>

</tbody>

</table>

<hr>

<h2>🌐 Comunicación con la API</h2>

<p>

Todas las operaciones realizadas por la aplicación son procesadas por la API REST de VitalTrace.

La aplicación nunca accede directamente a la base de datos ni implementa reglas clínicas propias.

</p>

<details open>

<summary><strong>🔓 Autenticación</strong></summary>

| Método | Endpoint | Descripción |
|---------|----------|-------------|
| POST | `/auth/login` | Inicio de sesión |
| POST | `/auth/logout` | Cerrar sesión |
| GET | `/auth/me` | Usuario autenticado |
| POST | `/auth/activate-account` | Activación inicial |
| POST | `/auth/resend-code` | Reenvío del código |
| POST | `/auth/forgot-password` | Recuperación de contraseña |
| POST | `/auth/reset-password` | Restablecimiento |

</details>

<details>

<summary><strong>👤 Paciente</strong></summary>

| Recurso | Descripción |
|----------|-------------|
| Profile | Información personal |
| Measurements | Registro y consulta de mediciones |
| Treatments | Tratamientos activos |
| Clinical History | Historial clínico |
| Appointments | Citas médicas |
| Notifications | Centro de notificaciones |

</details>

<details>

<summary><strong>👨‍👩‍👦 Familiares</strong></summary>

| Recurso | Descripción |
|----------|-------------|
| Authorized Patients | Pacientes autorizados |
| Measurements | Consulta de mediciones |
| Treatments | Consulta de tratamientos |
| Appointments | Consulta de citas |

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

<td>Koala o superior</td>

</tr>

<tr>

<td>JDK</td>

<td>17</td>

</tr>

<tr>

<td>Android SDK</td>

<td>API 35</td>

</tr>

<tr>

<td>Gradle</td>

<td>Incluido en el proyecto</td>

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

Antes de ejecutar la aplicación es necesario configurar la dirección de la API dentro del proyecto.

</p>

```kotlin
object NetworkConfig {

    const val BASE_URL = "http://TU_IP:8000/api/v1/"

}
```

<p>

En un dispositivo físico la dirección debe corresponder a la IP del servidor dentro de la misma red local.

Para ambientes de producción únicamente deberá modificarse la URL base.

</p>

<hr>

<h3>▶️ Ejecutar la aplicación</h3>

```bash
./gradlew assembleDebug
```

o directamente desde Android Studio utilizando la configuración **app**.

<hr>

<h2>📂 Organización del proyecto</h2>

<p>

La estructura del proyecto mantiene una separación por responsabilidades para facilitar el mantenimiento y la escalabilidad del código.

</p>

<pre>

app/

├── core/

│

├── data/

│

├── di/

│

├── domain/

│

├── features/

│

├── navigation/

│

├── ui/

│

└── MainActivity.kt

</pre>

<hr>

<h2>📦 Organización por funcionalidades</h2>

<p>

Cada funcionalidad implementa la misma estructura interna.

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

</pre>

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

<td>Gson</td>

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

<td>Gson</td>

<td>Serialización JSON.</td>

</tr>

<tr>

<td>OkHttp</td>

<td>Interceptores y manejo de peticiones.</td>

</tr>

<tr>

<td>Coil</td>

<td>Carga eficiente de imágenes.</td>

</tr>

<tr>

<td>DataStore</td>

<td>Persistencia local.</td>

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

<li>Manejo uniforme de respuestas de la API.</li>

<li>Componentes reutilizables siguiendo Material Design 3.</li>

</ul>

<hr>
<h2>☁️ Despliegue</h2>

<p>

La aplicación puede ejecutarse tanto en entornos de desarrollo como de producción modificando únicamente la configuración correspondiente a la URL base de la API.

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

<td>Servidor local ejecutando la API de VitalTrace dentro de la misma red.</td>

</tr>

<tr>

<td>Producción</td>

<td>API desplegada en el servidor oficial del proyecto.</td>

</tr>

</tbody>

</table>

<p>

La compilación para producción se realiza utilizando el tipo de compilación <strong>Release</strong>, mientras que durante el desarrollo se utiliza <strong>Debug</strong>.

</p>

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

<li>Actualizar información de perfil.</li>

<li>Consultar y administrar notificaciones.</li>

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

</ul>

</details>

<details>

<summary><strong>🔐 Seguridad</strong></summary>

<ul>

<li>Las pantallas protegidas requieren autenticación.</li>

<li>La sesión se valida antes de acceder a recursos protegidos.</li>

<li>La aplicación elimina la información local al cerrar sesión.</li>

<li>Los permisos dependen del rol autenticado.</li>

<li>La autorización final siempre es validada por la API.</li>

</ul>

</details>

<hr>

</tbody>

</table>

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

<li>Persistencia segura de sesión mediante DataStore.</li>

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
