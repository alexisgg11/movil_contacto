# Contacto · Panel de proveedores

Proyecto del curso **Tecnología y Desarrollo Móvil**. Aplicación Android nativa en **Kotlin y Jetpack Compose**, API independiente en **Java y Spring Boot**, y persistencia en **PostgreSQL**.

## Qué incluye

- Menú con **Resumen** y **Proveedores**.
- Panel con total de proveedores, activos e inactivos.
- Registro y edición en un modal con seis campos: empresa, contacto, teléfono, correo, dirección y rubro. Rubro es un selector con opciones; si un registro antiguo tiene otro rubro, se conserva como opción actual.
- Búsqueda con icono de lupa junto al campo, filtros por estado y páginas de 5 registros ordenadas por código ascendente.
- Filas compactas con detalle expandible en celular; tabla desde 840 dp de ancho disponible.
- Desactivación y reactivación con confirmación. **No se borran los registros desde la API ni desde Android.**
- Validaciones de campos obligatorios, longitudes, teléfono y correo.
- Empresas únicas, ignorando mayúsculas y espacios repetidos, incluso si están inactivas.
- Modales de validación, confirmación, éxito y error.

El registro es único por empresa; teléfono y correo pueden compartirse entre empresas. La edición excluye el propio identificador al comprobar duplicados y conserva su estado.

## Cómo funciona

```text
Android / Compose → ViewModel → Repositorio → HTTP / JSON
                                                ↓
Java / Spring Boot: Controller → Service → Repository / JPA → PostgreSQL
                                   ↕
                                 Mapper
                                   ↕
                             DTO de entrada/salida
```

Android no se conecta directamente a PostgreSQL. El backend debe estar activo para consultar, guardar, editar y cambiar el estado. La app no tiene modo sin conexión ni utiliza Room. Cerrar app o servidor no borra los datos.

## Instalación fácil en Windows

### 1. Requisitos

| Herramienta | Requisito del proyecto |
|---|---|
| [Android Studio](https://developer.android.com/studio) | Compatible con Android Gradle Plugin **9.4.1**; verificado con Android Studio **2026.1.4**. |
| Android SDK | **API 37** y las herramientas que solicite la sincronización. |
| Java | El daemon Gradle Android está configurado con **JDK 25**, descargable durante la sincronización. Los scripts usan el JDK de Android Studio o un `JAVA_HOME` válido. El backend compila código Java 17. |
| [PostgreSQL 18](https://www.postgresql.org/download/windows/) | Servidor y herramientas de línea de comandos; puerto predeterminado **5432**. Conserva la contraseña elegida para `postgres`. |
| [Git para Windows](https://git-scm.com/downloads/win) | Para clonar; alternativamente descarga ZIP. |

Necesitas Internet para descargar dependencias. No instales Gradle ni Maven por separado: el repositorio incluye Gradle Wrapper.

### 2. Descargar y abrir

Desde PowerShell:

```powershell
New-Item -ItemType Directory -Force C:\Android | Out-Null
git clone https://github.com/alexisgg11/movil_contacto.git C:\Android\contacto
cd C:\Android\contacto
```

Usa otra carpeta vacía si esa ruta ya existe. Se recomienda una ruta corta. Sin Git, usa **Code → Download ZIP** y descomprime.

En Android Studio:

1. **Open** o **File → Open** y selecciona la raíz `contacto`, que contiene `settings.gradle.kts`.
2. No crees otro proyecto Empty Activity ni abras solo `app` o `backend`.
3. Espera **Gradle Sync** y acepta las descargas necesarias.
4. Instala **API 37** en **Tools → SDK Manager**. Si no aparece, actualiza Android Studio y sus herramientas.
5. Si lo solicita, permite descargar **JDK 25**. La configuración de Java de Gradle está en **Settings → Build, Execution, Deployment → Build Tools → Gradle**.

Android Studio genera `local.properties` con la ruta del SDK de tu propia PC. Ese archivo no se comparte.

### 3. Preparar PostgreSQL y tus credenciales

El servicio PostgreSQL debe estar iniciado. Hay dos opciones; elige una.

**Opción A: preparación automática en Windows (script opcional).** Para una instalación nueva, sin `.env` ni base creados:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Preparar-Postgres.ps1
```

Introduce la contraseña del administrador `postgres`. El script crea la base `contacto_proveedores`, el usuario dedicado `contacto_app` y un **`.env` en la raíz**, con contraseña aleatoria. No tienes que copiar la plantilla si eliges esta opción. El script no sobrescribe un `.env` existente ni reutiliza bases o usuarios existentes automáticamente.

La ruta de `psql.exe` predeterminada es `C:\Program Files\PostgreSQL\18\bin\psql.exe`; ajusta `$psql` en el script si tu instalación es diferente.

**Opción B: configuración manual sin scripts.** Crea en pgAdmin una base `contacto_proveedores` con un usuario que tenga permiso para crear sus tablas. Puedes crear un usuario dedicado ejecutando estas sentencias por separado en Query Tool, conectado como administrador:

```sql
CREATE ROLE contacto_app LOGIN PASSWORD 'TU_CLAVE_LOCAL';
```

```sql
CREATE DATABASE contacto_proveedores OWNER contacto_app;
```

Sustituye la contraseña del ejemplo por una tuya. Si el usuario o la base ya existen, usa sus credenciales; no vuelvas a crearlos ni borres datos.

Después, desde la raíz del proyecto:

```powershell
Copy-Item .env.example .env
```

Abre `.env` y completa la URL, usuario y contraseña reales de **tu propia instalación**. La plantilla pública contiene solamente valores de ejemplo. No cambies la contraseña únicamente en el archivo si el usuario PostgreSQL tiene otra contraseña: deben coincidir.

El formato es `CLAVE=valor`, sin `export`, comillas ni comentarios al final de la línea. Spring Boot lee este archivo como Java Properties. Para una contraseña con barra invertida, escríbela escapada como `\\`; los valores con saltos de línea no forman parte del formato de la plantilla.

No crees tablas ni importes respaldos: Flyway aplica las migraciones al iniciar. Clonar el proyecto no copia los proveedores de otra PC.

### 4. Iniciar Spring Boot

Desde la raíz del proyecto, con un JDK compatible configurado, puedes iniciar **sin nuestros scripts**:

```powershell
.\gradlew.bat -p backend run
```

Gradle ejecuta el backend con la raíz como directorio de trabajo y Spring Boot carga `.env` directamente. No hay que exportar sus variables manualmente. Si prefieres ejecutar la clase principal desde un IDE, usa la raíz `contacto` como directorio de trabajo; también puedes indicar una ruta explícita con la variable `CONTACTO_ENV_FILE`.

Como alternativa cómoda en Windows:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Iniciar-Backend.ps1
```

Ese script prepara Java y la ruta temporal corta que algunas instalaciones de Windows necesitan. No lee ni descifra credenciales: Spring Boot carga `.env`. Si el inicio directo falla por `JAVA_HOME` o sockets/rutas temporales, usa este acceso o ajusta tu entorno Java.

Espera **`Tomcat started on port 8080`** y **`Started ProveedoresApplication`**. La primera ejecución descarga dependencias. Mantén la terminal abierta; `Ctrl+C` detiene el servidor. No inicia PostgreSQL ni Android.

Comprueba en el navegador de la PC:

```text
http://localhost:8080/api/proveedores
```

Una base sin registros devuelve `[]`.

### 5. Ejecutar Android

1. Abre **Tools → Device Manager** y crea/inicia un emulador, por ejemplo Pixel 7. Mínimo: **Android 7 / API 24**.
2. Selecciona **app** y pulsa **Run ▶**.
3. Concede el permiso de red local cuando aparezca.
4. Pulsa **Registrar proveedor** para abrir el modal. La edición usa el icono de lápiz; el botón de estado desactiva o reactiva después de confirmar.

El emulador usa `http://10.0.2.2:8080/` para acceder al backend de la PC. No uses `localhost` dentro del emulador: representa al propio teléfono virtual.

## Qué hacer cada día

1. Comprueba el servicio PostgreSQL.
2. Inicia el backend si no está activo.
3. Ejecuta Android.

No recrees la base ni ejecutes dos servidores en el puerto 8080. Al actualizar desde una versión anterior, reinicia el backend y recompila Android: esta versión añade edición y estado.

## ¿Dónde están las credenciales?

```text
C:\Android\contacto\.env
```

- `.env` contiene tu configuración local y está excluido de Git. La contraseña está como texto: no compartas ni publiques ese archivo.
- `.env.example` sí se publica: sirve de plantilla y no contiene credenciales reales.
- Spring Boot importa `.env` directamente mediante `spring.config.import`; no depende de PowerShell para leerlo.
- `application.properties` utiliza `DB_URL`, `DB_USER` y `DB_PASSWORD`. Las variables de entorno del sistema pueden prevalecer sobre el archivo: revisa valores anteriores si la conexión no coincide con `.env`.
- Android solo conoce la URL de la API; no recibe la contraseña de PostgreSQL.
- Cada compañero usa su propio `.env` y su propia base.
- El XML cifrado de versiones anteriores dejó de utilizarse. En el equipo original se conserva en `.local/` únicamente como respaldo local; no hace falta copiarlo a otras máquinas.

En Linux/macOS configura PostgreSQL, crea tu `.env` desde la plantilla y ejecuta `./gradlew -p backend run` desde la raíz. Los scripts de Windows son opcionales.

## ¿Son obligatorios los scripts?

| Script | ¿Obligatorio? | Alternativa |
|---|---|---|
| `Preparar-Postgres.ps1` | No. Automatiza la primera preparación en Windows. | Crear usuario/base con pgAdmin y completar `.env`. |
| `Iniciar-Backend.ps1` | No. Prepara Java y ejecuta el servidor. | `gradlew.bat -p backend run` o ejecutar la clase principal desde el IDE, con el entorno y directorio de trabajo correctos. |
| `Verificar.ps1` | No para usar la app. Agrupa compilación, lint y pruebas. | Ejecutar las tareas Gradle individualmente. |
| `common/Entorno-Java.ps1` | Funciones internas de los scripts anteriores. | No se ejecuta directamente ni hace falta para Spring Boot. |

Lo necesario es que estén funcionando PostgreSQL y el backend. La manera de iniciarlos puede ser manual, desde el IDE o mediante los scripts.

## Teléfono físico

Conecta PC y teléfono a la misma Wi-Fi. Obtén la IPv4 de la PC con `ipconfig` y añade, en `%USERPROFILE%\.gradle\gradle.properties`, tu dirección real:

```properties
API_BASE_URL=http://192.168.1.50:8080/
```

Permite el acceso al puerto TCP 8080 en la red privada y recompila. Para volver al emulador, elimina la propiedad y recompila. La propiedad personal también afecta a otros proyectos que utilicen ese nombre.

HTTP está permitido solo en **debug**. Para una distribución release configura HTTPS. Esta versión académica no implementa autenticación ni roles; no es un CRM listo para exponer públicamente.

## Estructura y responsabilidades

```text
app/src/main/java/com/example/contactoproveedores/
  MainActivity.kt                    Entrada: inicializa Compose y llama a ProveedoresApp
  di/AppContainer.kt                 Construcción de dependencias
  domain/
    model/                           Proveedor, Campo, FiltroEstado y acceso a campos
    validation/                      Reglas de formulario y normalización de empresa
    repository/                      Contrato de persistencia y error de operación
  data/
    remote/                          Cliente HTTP y serialización JSON
    repository/                      Implementación del repositorio usando la API
  viewmodel/
    ProveedoresViewModel.kt          Coordina carga, registro, edición y estado
    ProveedoresState.kt              Estado de pantalla y resultados filtrados
    AccionesProveedores.kt           Eventos que recibe la UI
    MensajeModal.kt                  Datos de los mensajes
  ui/
    ProveedoresApp.kt                Conecta dependencias, ViewModel y pantalla
    screens/                         Composición del panel
    navigation/                      Menú y secciones
    formulario/                      Modal de formulario y componente de campo
    components/                      Un archivo por componente reutilizable
    theme/                           Paleta, tipografía y tema
backend/src/main/java/com/example/proveedores/
  ProveedoresApplication.java         Entrada Spring Boot
  controller/                        Rutas HTTP
  service/                           Operaciones y transacciones
  repository/                        Consultas JPA y comprobación de duplicados
  entity/                            Entidad persistente; no depende de DTO ni HTTP
  dto/                               Contratos de entrada y salida
  mapper/                            Conversión DTO ↔ entidad
  validation/                        Normalización de empresa
  exception/                         Errores de negocio y traducción HTTP
backend/src/main/resources/
  application.properties             Configuración por variables de entorno
  db/migration/                      V1 tabla, V2 unicidad, V3 estado y clave normalizada
scripts/
  Preparar-Postgres.ps1               Preparación local
  Iniciar-Backend.ps1                 Inicio del servidor
  Verificar.ps1                      Verificaciones
  common/                            Preparación de Java compartida por los scripts
```

El ViewModel depende del contrato del dominio, no del cliente HTTP. Las entidades Java no importan DTO; el mapper hace esa conversión. Los componentes no ejecutan SQL ni peticiones de red. Las funciones privadas relacionadas con una misma responsabilidad permanecen juntas; no se crean capas vacías ni un archivo por cada método trivial.

## API

| Método | Ruta | Operación |
|---|---|---|
| GET | `/api/proveedores` | Lista por ID descendente; parámetros opcionales `busqueda` y `activo` |
| GET | `/api/proveedores/{id}` | Consulta un registro; 404 si no existe |
| POST | `/api/proveedores` | Registra; 201 o 409 si existe la empresa |
| PUT | `/api/proveedores/{id}` | Edita los seis datos; conserva el estado |
| PUT | `/api/proveedores/{id}/estado` | Cambia estado con `{"activo":false}` o `{"activo":true}` |
| DELETE | `/api/proveedores/{id}` | Compatibilidad con clientes antiguos: desactiva y devuelve 204; nunca borra |

POST y PUT de datos reciben:

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

Los textos obligatorios requieren al menos 2 caracteres; los máximos están definidos en el modelo Android y DTO Java. El teléfono admite de 7 a 15 dígitos, con prefijo `+`, espacios, guiones y paréntesis (máximo 20 caracteres). El correo se verifica por formato, no por existencia real.

La búsqueda, los filtros y la paginación Android operan sobre la lista cargada. El panel muestra 5 proveedores por página, ordenados por código ascendente; buscar o cambiar el filtro vuelve a la primera página. Registrar abre la página donde quedó el nuevo proveedor. En la API la búsqueda se ejecuta en la base de datos. Para directorios grandes, el siguiente paso es paginación del servidor y consultas remotas: actualmente Android descarga la lista completa.

## Migraciones y datos existentes

Flyway conserva el esquema versionado. V3 marca como activos los registros anteriores y añade una clave de empresa normalizada indexada para comprobar duplicados sin cargar toda la tabla. PostgreSQL impide duplicados incluso con solicitudes simultáneas.

Si una instalación anterior contiene duplicados, revisa esos datos antes de actualizar; las migraciones no borran ni fusionan registros automáticamente. `ddl-auto=validate` verifica el esquema y no recrea la base al iniciar.

## Datos de demostración y códigos

Con el backend encendido, puedes cargar diez proveedores **ficticios**:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Cargar-Demo.ps1
```

El archivo `scripts/datos/proveedores-demo.json` contiene los ejemplos, identificados por `(demo)`, con correos de ejemplo y teléfonos ficticios. El script omite empresas que ya existen; no modifica registros anteriores ni reinicia identificadores. Incluye dos proveedores inactivos para comprobar los filtros.

En una base vacía resultan 10 registros y dos páginas. En el equipo original se conservó el proveedor existente como primer registro y se añadieron diez ejemplos: **11 registros y tres páginas (5 + 5 + 1)**.

Los códigos se derivan de la clave persistente de PostgreSQL y no se renumeran al buscar, filtrar o desactivar. El reinicio puntual de la base local para la demostración no forma parte del arranque ni de las migraciones automáticas. Los saltos en identificadores pueden aparecer por pruebas o transacciones y no son un error de conteo.

## Verificaciones

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Verificar.ps1
```

Compila Android, ejecuta pruebas unitarias y lint, y prueba la API con **H2 aislado**, sin tocar la base real.

Con backend y emulador activos:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Verificar.ps1 -ConEmulador
```

La prueba de interfaz registra su propia empresa con prefijo `Proveedor CRM UI`, edita, desactiva, comprueba persistencia, reactiva y vuelve a desactivar. Ese registro queda inactivo para respetar la ausencia de borrado físico en la API. También comprueba el menú y la tabla en horizontal. Las capturas se copian a `/sdcard/crm-*.png` en el emulador; no se publican en Git.

## Problemas frecuentes

| Problema | Qué revisar |
|---|---|
| Registrar deshabilitado | Inicia el backend, concede permiso de red local y actualiza el panel. Se requiere una carga inicial correcta. |
| API no responde | Revisa terminal de Spring Boot y servicio PostgreSQL. |
| Contraseña rechazada | Usa la contraseña de `postgres` elegida en esta PC, no la de Windows. |
| Fallo de autenticación PostgreSQL | Revisa usuario/contraseña en `.env` y confirma que coincidan con PostgreSQL. El archivo XML anterior ya no se utiliza. |
| Puerto 8080 ocupado | No inicies dos instancias del backend. |
| SDK/Gradle incompatible | Android Studio compatible con AGP 9.4.1, API 37 y JDK configurado. |
| Emulador `offline` o `Device is already activating` | No pulses Run repetidamente. Detén el dispositivo y usa **Cold Boot** en Device Manager. |
| Emulador se cierra inesperadamente | Prueba arranque en frío y gráficos **Software** en su configuración. Evita Wipe Data si necesitas conservar datos locales. |
| Error de ruta temporal Java | Usa una ruta corta y los scripts del proyecto. |
| No aparecen datos de otra PC | GitHub comparte código; cada instalación tiene su propia base. |
| Empresa duplicada pero no aparece | Selecciona **Todos**: puede estar inactiva. Edítala o reactívala. |
