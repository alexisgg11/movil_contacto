# Contacto Proveedores

Proyecto del curso **Tecnología y Desarrollo Móvil**: app Android en **Kotlin y Jetpack Compose**, backend en **Java y Spring Boot**, y almacenamiento persistente en **PostgreSQL**.

## Funcionalidades

- Seis campos obligatorios: empresa, persona de contacto, teléfono, correo, dirección y rubro.
- Registro con validación en Android y en el servidor.
- Empresa única: se rechaza el mismo nombre aunque cambien mayúsculas o espacios. Teléfono y correo pueden compartirse entre empresas.
- Modales para formulario inválido, duplicados, registro/eliminación exitosos y errores de conexión.
- Directorio con tarjetas e iconos, diseñado para celular.
- Tarjetas contraídas: pulsa **Ver detalle** para mostrar teléfono, correo y dirección, y **Ocultar detalle** para contraerlas.
- Búsqueda por cualquiera de los campos de la lista cargada.
- Pulsa **Buscar** o la acción de búsqueda del teclado para recibir un modal si no hay coincidencias. El filtrado continúa al escribir sin interrumpirte con ventanas.
- Eliminación con confirmación.
- Estados de carga, errores y reintento.
- Los datos permanecen después de cerrar la app y reiniciar el backend.

No incluye edición de proveedores guardados ni funcionamiento sin conexión.

Empresa, contacto, dirección y rubro requieren entre 2 caracteres y su límite de longitud. El teléfono debe tener de 7 a 15 dígitos y admite prefijo `+`, espacios, guiones y paréntesis (máximo 20 caracteres). Se verifica el formato del correo; no su existencia real.

La migración V2 añade un índice único de empresas normalizadas en PostgreSQL, protegiendo también frente a registros simultáneos. Si una instalación antigua ya tiene empresas duplicadas, revisa esos registros antes de actualizar: la migración se detiene y no los elimina automáticamente.

## Cómo funciona

```text
Android (Kotlin + Compose)
    │ HTTP / JSON
    ▼
Backend (Java + Spring Boot, puerto 8080)
    │ JPA / Hibernate
    ▼
PostgreSQL (puerto 5432, base contacto_proveedores)
```

**Android no accede directamente a PostgreSQL.** Para consultar, registrar o eliminar deben estar funcionando PostgreSQL y el backend. Apagarlos no borra los datos.

## Instalación fácil en Windows

Los scripts de instalación usan PowerShell y cifrado de credenciales de Windows. Cada compañero prepara su propia base en su PC.

### 1. Instalar los requisitos

| Herramienta | Qué necesita este proyecto |
|---|---|
| [Android Studio](https://developer.android.com/studio) | Versión compatible con Android Gradle Plugin **9.4.1**. Verificado con Android Studio **2026.1.4**. |
| Android SDK | Instalar **API 37** desde SDK Manager y las herramientas que solicite Gradle. |
| Java/JDK | El daemon Gradle de Android está configurado con **JDK 25** y puede descargarlo durante la sincronización. Los scripts usan el JDK incluido en Android Studio o un `JAVA_HOME` válido. |
| [PostgreSQL 18 para Windows](https://www.postgresql.org/download/windows/) | Instalar servidor y herramientas de línea de comandos, conservar la contraseña del usuario `postgres` y usar el puerto **5432**. |
| [Git para Windows](https://git-scm.com/downloads/win) | Para clonar; también puedes descargar ZIP desde GitHub. |

Necesitas Internet para descargar las dependencias inicialmente. **No instales Gradle ni Maven por separado**: se incluye Gradle Wrapper. El código del backend usa Java 17; esto es distinto del JDK 25 configurado para ejecutar Gradle en Android.

### 2. Descargar

En PowerShell:

```powershell
New-Item -ItemType Directory -Force C:\Android | Out-Null
git clone https://github.com/alexisgg11/movil_contacto.git C:\Android\contacto
cd C:\Android\contacto
```

Si esa carpeta existe, elige otra vacía. Se recomienda una ruta corta para evitar problemas con archivos temporales de Java en Windows.

**Sin Git:** pulsa **Code → Download ZIP**, descomprime y busca la carpeta que contiene `settings.gradle.kts`. No crees otro proyecto Empty Activity ni copies solamente `app`.

### 3. Abrir en Android Studio

1. Pulsa **Open**, o **File → Open** si ya tienes otro proyecto abierto.
2. Selecciona la raíz `C:\Android\contacto`, no `app` ni `backend`.
3. Espera a que termine **Gradle Sync**. Acepta la descarga de herramientas que solicite.
4. En **Tools → SDK Manager → SDK Platforms**, instala **Android API 37**. Si no aparece, actualiza Android Studio y las herramientas del SDK.
5. Si se solicita el JDK del daemon, permite descargar **JDK 25**. Revisa Java de Gradle en **Settings → Build, Execution, Deployment → Build Tools → Gradle** si falla la sincronización.

Android Studio crea `local.properties` con la ruta del SDK de tu PC; ese archivo no se comparte.

### 4. Preparar PostgreSQL — solo la primera vez

Comprueba en **Servicios** de Windows que PostgreSQL esté iniciado. En la terminal de Android Studio, selecciona PowerShell y ejecuta desde la raíz:

```powershell
cd C:\Android\contacto
powershell -ExecutionPolicy Bypass -File .\scripts\Preparar-Postgres.ps1
```

Introduce la contraseña del usuario **postgres** de tu instalación. La entrada permanece oculta; es normal que no aparezcan letras.

El script crea la base `contacto_proveedores`, el usuario `contacto_app` y `.local/db-credential.xml` con credenciales cifradas. El mensaje esperado es **«Base contacto_proveedores creada»**.

**No crees tablas ni importes respaldos manualmente**: Flyway crea la tabla cuando inicia el backend. Cada instalación comienza vacía, sin los contactos del autor.

El script busca `psql.exe` en `C:\Program Files\PostgreSQL\18\bin\psql.exe`. Si lo instalaste en otra ubicación, ajusta la variable `$psql` al inicio de `scripts/Preparar-Postgres.ps1`.

Las credenciales están cifradas para el usuario de Windows de ese equipo. No copies el archivo de otro compañero. Si la base y credenciales locales ya están preparadas, el script informa de ello. Si existen esos nombres sin una configuración local compatible, se detiene para no modificar datos existentes. No borres bases sin revisar su contenido.

### 5. Iniciar el backend

Desde la raíz del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Iniciar-Backend.ps1
```

Espera **`Tomcat started on port 8080`** y **`Started ProveedoresApplication`**. La primera ejecución puede tardar por las descargas.

**Mantén esa terminal abierta.** Este comando inicia Spring Boot; no inicia PostgreSQL ni Android. Para detenerlo, pulsa `Ctrl+C`.

Comprueba la API abriendo en el navegador de la PC:

```text
http://localhost:8080/api/proveedores
```

Si no hay registros, debe aparecer `[]`: el servidor respondió correctamente.

### 6. Ejecutar Android

1. Abre **Tools → Device Manager** y crea un emulador, por ejemplo Pixel 7.
2. Descarga una imagen de Android compatible e inicia el emulador. El mínimo de la app es **Android 7.0 / API 24**.
3. Selecciona la configuración **app** y pulsa **Run ▶**.
4. Si aparece la solicitud de permiso de red local, concédelo para acceder al servidor de la PC.
5. Completa los seis campos, agrega un proveedor y comprueba que aparezca en el directorio.

El emulador usa la dirección predeterminada **`http://10.0.2.2:8080/`** para acceder a la PC. No la reemplaces por `localhost`, que dentro del emulador representa al propio dispositivo virtual.

## Qué ejecutar cada día

Después de la primera instalación:

1. Comprueba que PostgreSQL esté funcionando; normalmente inicia como servicio de Windows.
2. Ejecuta `scripts/Iniciar-Backend.ps1` si el servidor no está activo.
3. Ejecuta la app desde Android Studio.

No repitas la creación de la base. No inicies otro backend si uno ya está escuchando en el puerto 8080. Cerrar la app o el backend no borra los proveedores.

## Usar un teléfono físico

1. Conecta la PC y el teléfono a la misma red Wi-Fi.
2. Ejecuta `ipconfig` y busca la IPv4 de la PC en esa red.
3. En tu archivo personal `%USERPROFILE%\.gradle\gradle.properties`, agrega esta propiedad sustituyendo la IP:

   ```properties
   API_BASE_URL=http://192.168.1.50:8080/
   ```

4. Permite al backend el acceso en la red privada si Windows lo solicita. El teléfono debe poder acceder al puerto TCP 8080.
5. Sincroniza y vuelve a compilar/ejecutar la app: la dirección se incorpora durante la compilación.

Para volver al emulador, elimina la propiedad y recompila. Esta propiedad personal afecta a otros proyectos que usen el mismo nombre.

HTTP sin cifrar se permite solo en **debug**. Para distribuir una versión **release**, configura HTTPS. El proyecto académico no incluye autenticación; úsalo en una red de pruebas.

## Solución de problemas

| Problema | Solución |
|---|---|
| Agregar deshabilitado o error de conexión | Inicia el backend, concede permiso de red local y pulsa **Actualizar**. Agregar requiere una carga inicial correcta y se deshabilita mientras hay una operación. |
| El navegador no abre la API | Revisa la terminal del backend y el servicio PostgreSQL. |
| Contraseña rechazada en la preparación | Usa la contraseña de `postgres` de tu instalación, no la de Windows. |
| Se solicita ejecutar Preparar-Postgres | Ejecuta la preparación en esta PC y con tu usuario de Windows. |
| No se encuentra PostgreSQL 18 | Verifica la instalación y ajusta la ruta de `psql.exe` si corresponde. |
| Puerto 8080 ocupado | Comprueba si ya hay un backend abierto. No ejecutes dos instancias. |
| SDK o Gradle incompatible | Usa Android Studio compatible con AGP 9.4.1, API 37 y el JDK configurado. |
| `Device is already activating` o emulador que no conecta | No pulses Run repetidamente. Detén el dispositivo desde Device Manager y usa **Cold Boot**. Comprueba con `C:\Android\Sdk\platform-tools\adb.exe devices` que aparezca como `device`, no `offline`. Si tu SDK está en otra ruta, adapta el comando. Evita **Wipe Data**, que borra los datos locales del emulador. |
| Error de socket/ruta temporal de Java | Usa una ruta corta y los scripts del proyecto, que configuran los temporales. |
| No aparecen datos de otro equipo | Cada PC tiene su propia base; GitHub comparte el código, no registros ni credenciales. |
| El teléfono físico no conecta | Revisa IP de la PC, recompilación, misma Wi-Fi y acceso al puerto 8080. |

## Estructura y responsabilidades

```text
app/                          Aplicación Android
  src/main/java/.../
    MainActivity.kt           Entrada: inicia ProveedoresApp
    ui/                       Pantalla, componentes, tema y permiso de red
    viewmodel/                Estado y coordinación de acciones
    domain/                   Modelo, validación y contrato de repositorio
    data/                     Peticiones HTTP y conversión JSON
  src/main/res/drawable/       Iconos vectoriales
backend/                      Proyecto Java independiente
  src/main/java/.../
    controller/               Endpoints HTTP
    service/                  Operaciones y transacciones
    repository/               Acceso mediante Spring Data JPA
    entity/                   Entidad persistente
    dto/                      Contratos de entrada/salida
    exception/                Respuestas de error
  src/main/resources/
    application.properties    Configuración del servidor
    db/migration/             Esquema versionado con Flyway
scripts/                      Preparación, inicio y verificaciones
gradle/                       Wrapper y versiones
```

MainActivity delega la interfaz. La UI recibe estado y emite acciones; el ViewModel coordina operaciones y el repositorio Android accede a la API.

## API

| Método | Ruta | Resultado |
|---|---|---|
| GET | `/api/proveedores` | Lista del más reciente al más antiguo |
| GET | `/api/proveedores?busqueda=andina` | Búsqueda sin distinguir mayúsculas |
| POST | `/api/proveedores` | Guarda y devuelve 201 con el registro |
| DELETE | `/api/proveedores/{id}` | Elimina y devuelve 204; 404 si no existe |

Ejemplo del cuerpo de un POST:

```json
{
  "empresa": "Distribuidora Andina",
  "contacto": "Ana Torres",
  "telefono": "987654321",
  "correo": "ana@andina.pe",
  "direccion": "Av. Primavera 120, Lima",
  "rubro": "Suministros"
}
```

## Verificar el proyecto

Desde PowerShell en la raíz:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Verificar.ps1
```

Compila Android, ejecuta pruebas unitarias y lint, y prueba la API. El backend usa **H2 aislado para las pruebas**; no modifica los registros de PostgreSQL.

Con backend y emulador encendidos, puedes incluir la prueba de interfaz:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Verificar.ps1 -ConEmulador
```

La prueba de interfaz crea y elimina su propio proveedor.

Para verificar persistencia manualmente: agrega un proveedor, cierra app y servidor, vuelve a iniciarlos y busca el registro. Elimínalo y comprueba después de reiniciar que no reaparezca.

## Configuración manual del backend

El servidor admite variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`. Con ellas configuradas y un JDK compatible disponible, ejecuta desde la raíz:

```powershell
.\gradlew.bat -p backend run
```

La instalación automatizada es para Windows. En Linux o macOS prepara PostgreSQL y las variables manualmente, y ejecuta `./gradlew -p backend run`.

## Archivos locales excluidos de Git

No se comparten credenciales `.local/`, rutas del SDK `local.properties`, configuración personal del IDE, compilaciones ni temporales. El repositorio no incluye una copia de la base de datos ni contactos reales.
