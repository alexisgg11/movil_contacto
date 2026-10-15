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
    model/                           Proveedor, Campo, RubroProveedor, FiltroEstado y acceso a campos
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
  Cargar-Demo.ps1                    Carga opcional de proveedores ficticios
  datos/                             Ejemplos JSON
  common/                            Entorno Java y lectura/escritura JSON compartidos
```

El ViewModel depende del contrato del dominio, no del cliente HTTP. Las entidades Java no importan DTO; el mapper hace esa conversión. Los componentes no ejecutan SQL ni peticiones de red. Las funciones privadas relacionadas con una misma responsabilidad permanecen juntas; no se crean capas vacías ni un archivo por cada método trivial.

## Guía para modificar el proyecto

Las rutas de esta sección son relativas a la raíz del repositorio: funcionan aunque cada compañero lo clone en otra ubicación. En Android Studio cambia la vista **Android → Project** para recorrer la estructura real. `Ctrl+Shift+N` permite buscar un archivo por su nombre.

### Qué significa cada capa

La **vista** es `ui/`: funciones `@Composable` que dibujan la pantalla con los datos recibidos. En Compose un componente se escribe en Kotlin, en lugar de combinar HTML y CSS. `Modifier` configura tamaño, márgenes y distribución; `Row`, `Column` y `Box` organizan el contenido. Los tamaños de distribución usan `dp` y el texto usa `sp`.

El **ViewModel** recibe las acciones de la vista, valida y coordina operaciones. Su estado observable se convierte en lo que muestra Compose; al cambiar ese estado, Compose actualiza la interfaz. El **dominio** contiene modelos, reglas y el contrato del repositorio. La capa **data** implementa ese contrato mediante HTTP y JSON. El backend tiene sus propias capas: controlador HTTP, servicio de negocio, repositorio JPA y entidad de PostgreSQL.

No hay una carpeta llamada `view`: esa responsabilidad corresponde a `ui`. Tampoco hay una entidad PostgreSQL dentro de Android: `Proveedor.kt` es el modelo móvil y `Proveedor.java` es la entidad persistente del servidor. Sus repositorios tienen el mismo nombre, pero son contratos diferentes para cada aplicación.

### Android: rutas de entrada, datos y lógica

En esta tabla, la base de todas las rutas es **`app/src/main/java/com/example/contactoproveedores/`**.

| Archivo dentro de esa base | Responsabilidad y qué modificar aquí |
|---|---|
| `MainActivity.kt` | Entrada Android; `onCreate()` activa Compose y llama a `ProveedoresApp()`. Mantenerla pequeña. |
| `ui/ProveedoresApp.kt` | Aplica el tema, obtiene el ViewModel, observa el estado y conecta sus métodos con las acciones de pantalla. |
| `di/AppContainer.kt` | Construye y conecta cliente HTTP y repositorio. Cambiar aquí una implementación de persistencia. |
| `viewmodel/ProveedoresViewModel.kt` | Lógica de interacción: `nuevo()`, `abrirEdicion()`, `cerrarFormulario()`, `editar()`, `guardar()`, `cargar()`, `buscar()`, `buscarConfirmado()`, `filtrar()`, `cambiarPagina()`, `confirmarEstado()`, `cambiarEstado()` y `cerrarModal()`. `operar()` centraliza ejecución asíncrona, carga y errores. |
| `viewmodel/ProveedoresState.kt` | Datos actuales de pantalla: formulario, lista, errores, filtros, página y modales. `resultados` filtra y ordena; `proveedoresPagina` recorta la página. Cambiar `tamanioPagina` para ajustar los 5 registros actuales. |
| `viewmodel/AccionesProveedores.kt` | Agrupa callbacks de la pantalla. Añadir aquí una acción que la UI necesite comunicar al ViewModel y conectarla en `ProveedoresApp.kt`. |
| `viewmodel/MensajeModal.kt` | Modelo del título y contenido de mensajes; el diseño del diálogo vive en `ModalResultado.kt`. |
| `domain/model/Proveedor.kt` | Modelo de datos móvil, marcado `@Serializable` para generar su conversión JSON automáticamente, y `normalizado()` para recortar espacios. No guarda directamente en la base. |
| `domain/model/Campo.kt` | Campos del formulario, etiquetas y longitudes máximas. |
| `domain/model/CamposProveedor.kt` | `valor()` y `actualizar()`: acceso al dato correspondiente a cada `Campo`. |
| `domain/model/RubroProveedor.kt` | Catálogo de opciones del selector de rubros. El servidor actualmente acepta rubro como texto; este catálogo no es una restricción de PostgreSQL. |
| `domain/model/FiltroEstado.kt` | Opciones Todos/Activos/Inactivos y regla `acepta()`. |
| `domain/validation/ValidarProveedor.kt` | `errores()`: reglas del formulario y mensajes por campo. |
| `domain/validation/NormalizarEmpresa.kt` | `clave()`: normaliza empresa para comparar duplicados. Mantenerla coherente con la normalización del backend. |
| `domain/repository/ProveedorRepository.kt` | Interfaz de operaciones `listar()`, `agregar()`, `actualizar()` y `cambiarEstado()`; no contiene HTTP. |
| `domain/repository/ErrorProveedor.kt` | Error de operación con mensaje y código HTTP opcional. |
| `data/repository/ApiProveedorRepository.kt` | Implementación móvil: elige métodos y rutas HTTP para cada operación y utiliza la conversión automática de Kotlin Serialization. |
| `data/remote/ClienteHttp.kt` | `solicitar()`: conexión, tiempos de espera, cabeceras, envío y lectura. Usa `Dispatchers.IO` para no bloquear la interfaz. |
| `data/remote/dto/GuardarProveedorRequest.kt` | Datos editables enviados al registrar/editar; excluye ID y estado, controlados por el servidor. Es un DTO `@Serializable`, no un conversor escrito a mano. |

### Android: dónde cambiar cada parte visual

Las siguientes rutas parten de **`app/src/main/java/com/example/contactoproveedores/ui/`**. Cada componente de la tabla tiene su propio archivo; para modificar su estructura abre ese archivo y edita su función `@Composable`.

| Archivo dentro de `ui/` | Qué controla |
|---|---|
| `theme/Color.kt` | Colores compartidos como `VerdeContacto`, `FondoPanel`, `TextoPrincipal`, `TextoSecundario`, `BordePanel` y `VerdeSuave`. |
| `theme/Theme.kt` | Asigna colores a los roles Material (`primary`, `surface`, etc.) y define los temas claro/oscuro. Algunos colores específicos se definen directamente aquí: revisar ambos temas al cambiar la paleta. |
| `theme/Type.kt` | Tipografía global: familia, tamaño, peso y altura de línea. Los estilos no redefinidos usan los valores de Material 3. |
| `screens/ProveedoresScreen.kt` | Orden general del panel, barra superior, botón de registro, lista y distribución adaptable. Aquí se decide usar tabla con ancho disponible de al menos `840.dp` y se invocan los componentes. El título Directorio no muestra un contador adicional. |
| `navigation/MenuPanel.kt` | Apariencia del menú lateral y sus opciones. |
| `navigation/SeccionPanel.kt` | Secciones disponibles y sus títulos; su contenido se selecciona en `ProveedoresScreen.kt`. |
| `formulario/ModalProveedor.kt` | Estructura del modal nuevo/editar: campos, zona desplazable y botones Guardar/Cancelar. |
| `formulario/CampoFormularioProveedor.kt` | Diseño de cada caja de texto, teclado y presentación del error. |
| `formulario/SelectorRubroProveedor.kt` | Desplegable de rubros, selección y presentación del error. Sus opciones vienen de `RubroProveedor.kt`. |
| `components/CabeceraDirectorio.kt` | Campo de búsqueda, lupa y controles de filtro. La lógica que responde a ellos está en el ViewModel. |
| `components/ResumenProveedores.kt` | Indicadores Total/Activos/Inactivos; recibe los números ya calculados. |
| `components/TarjetaProveedor.kt` | Estructura de la tarjeta/fila, código visible y apertura del detalle. Aquí se cambia la presentación del código, sin modificar el ID real en la base. |
| `components/InicialesEmpresa.kt` | Círculo con iniciales del nombre de empresa. |
| `components/EtiquetaEstado.kt` | Insignia Activo/Inactivo y sus colores. |
| `components/AccionesFilaProveedor.kt` | Iconos de editar y desactivar/reactivar; emite acciones, no guarda datos. |
| `components/DetalleProveedor.kt` | Datos mostrados al expandir: teléfono, correo y dirección. |
| `components/DatoContacto.kt` | Distribución de una fila de detalle: icono, etiqueta y valor. |
| `components/CabeceraTablaProveedores.kt` | Títulos y distribución de columnas en pantallas anchas. Mantener alineados sus anchos con la fila de `TarjetaProveedor.kt`. |
| `components/PaginacionProveedores.kt` | Flechas, texto de página y rango de registros. Los cálculos y la selección de registros están en `ProveedoresState.kt`. |
| `components/ConfirmarEstadoProveedor.kt` | Modal de confirmación antes de cambiar estado. |
| `components/ModalResultado.kt` | Diseño del modal de éxito/error/validación. El ViewModel establece sus mensajes. |
| `components/EstadoDirectorio.kt` | Mensajes de lista vacía, búsqueda sin coincidencias o error de carga. |
| `components/PermisoRedLocal.kt` | Solicitud del permiso de acceso a red local según la versión Android. |

Los iconos vectoriales están en **`app/src/main/res/drawable/ic_*.xml`** y se usan con `painterResource`. El tema visual se aplica con `MaterialTheme.colorScheme` y `MaterialTheme.typography`; los márgenes y formas específicos de una tarjeta o modal se cambian en ese componente. No hay una hoja CSS. Los textos visibles están actualmente en las funciones Kotlin; si se añade traducción, se deben extraer a recursos `res/values/strings.xml`.

### Backend Java: rutas y responsabilidades

La base de estas rutas es **`backend/src/main/java/com/example/proveedores/`**.

| Archivo dentro de esa base | Responsabilidad y qué modificar aquí |
|---|---|
| `ProveedoresApplication.java` | `main()` inicia Spring Boot. No añadir reglas de negocio aquí. |
| `controller/ProveedorController.java` | Rutas y métodos GET/POST/PUT/DELETE, recepción de DTO con `@Valid` y códigos de respuesta. Delega en el servicio. |
| `service/ProveedorService.java` | Reglas y transacciones: `listar()`, `obtener()`, `agregar()`, `actualizar()` y `cambiarEstado()`. `encontrar()` resuelve ausencia y `comprobarDuplicado()` comprueba empresa única excluyendo el propio ID. |
| `repository/ProveedorRepository.java` | Consultas JPA: `buscar()` y `existsByEmpresaClaveAndIdNot()`, además de las operaciones heredadas de `JpaRepository`. No contiene elementos de interfaz. |
| `entity/Proveedor.java` | Entidad, columnas y estado persistente. `actualizar()` modifica datos y `cambiarEstado()` modifica el estado. Cambiar una columna requiere también una migración SQL. |
| `dto/ProveedorRequest.java` | Datos de registro/edición, limpieza de entrada y validaciones del servidor mediante anotaciones. |
| `dto/ProveedorResponse.java` | Campos devueltos al cliente. No exponer información interna o credenciales aquí. |
| `dto/EstadoProveedorRequest.java` | Contrato y validación de la petición de cambio de estado. |
| `mapper/ProveedorMapper.java` | `crear()`, `actualizar()` y `respuesta()` convierten entre DTO y entidad. |
| `validation/NormalizarEmpresa.java` | `clave()` normaliza la empresa para su unicidad. |
| `exception/ProveedorDuplicadoException.java` | Error de negocio de empresa duplicada. |
| `exception/ProveedorNoEncontradoException.java` | Error de negocio cuando no existe un ID. |
| `exception/ApiExceptionHandler.java` | Convierte errores de validación, duplicidad, ausencia, integridad y JSON inválido en respuestas HTTP con mensaje. |

El esquema está en **`backend/src/main/resources/db/migration/`**: `V1__crear_proveedores.sql`, `V2__empresa_unica.sql` y `V3__estado_y_clave_empresa.sql`. Para cambiar una base que ya ha ejecutado esas versiones, añadir una **nueva migración V4, V5, etc.**; no editar migraciones aplicadas, porque Flyway verifica su historial.

### Configuración, dependencias y scripts

| Ruta desde la raíz | Para qué sirve / cuándo editarla |
|---|---|
| `.env` | Credenciales reales y puerto de cada equipo; privado, ignorado por Git. No existe en un clon nuevo hasta crearlo. |
| `.env.example` | Plantilla pública sin secretos; actualizarla si se añaden variables requeridas. |
| `.gitignore` | Exclusiones de secretos, configuración local y archivos generados. |
| `backend/src/main/resources/application.properties` | Importación de `.env`, conexión por variables, Flyway y comportamiento JPA. |
| `app/build.gradle.kts` | SDK, versión Android, dependencias del módulo y lectura de `API_BASE_URL` para generar `BuildConfig`. La URL es configuración de conexión, no una contraseña. |
| `gradle/libs.versions.toml` | Catálogo de versiones y bibliotecas Android. |
| `build.gradle.kts` / `settings.gradle.kts` | Plugins y configuración raíz Android. |
| `backend/build.gradle.kts` / `backend/settings.gradle.kts` | Dependencias Java/Spring Boot, pruebas y tarea `run` del backend independiente. |
| `gradle/wrapper/gradle-wrapper.properties` | Versión Gradle del Wrapper; los archivos `gradlew`/`gradlew.bat` lo ejecutan. |
| `gradle/gradle-daemon-jvm.properties` | Requisitos de Java del daemon Gradle Android. |
| `gradle.properties` | Opciones compartidas de construcción; nunca guardar contraseñas. Para una URL personal usar el archivo Gradle del usuario descrito en Teléfono físico. |
| `local.properties` | Ruta del SDK en cada máquina; generado localmente e ignorado por Git. |
| `app/src/main/AndroidManifest.xml` | Declaración de aplicación, actividad y permisos Android. |
| `app/src/debug/AndroidManifest.xml` | Permiso de HTTP para desarrollo debug. |
| `app/src/main/res/values/strings.xml` | Recursos de texto Android, como el nombre de la aplicación; los textos del panel aún están en Compose. |
| `app/src/main/res/values/themes.xml` / `colors.xml` | Tema y recursos Android de la plantilla. Para los colores del panel Compose usar `ui/theme/`, no cambiar solamente estos XML. |
| `app/src/main/res/mipmap-*/` | Iconos de lanzamiento de la app, en diferentes densidades. |
| `app/src/main/res/xml/backup_rules.xml` / `data_extraction_rules.xml` | Reglas de copia de seguridad y transferencia de datos Android; no configuran PostgreSQL. |
| `scripts/Preparar-Postgres.ps1` | Preparación opcional de usuario/base y `.env`. |
| `scripts/Iniciar-Backend.ps1` | Atajo opcional para preparar Java e iniciar el backend. |
| `scripts/Verificar.ps1` | Agrupa compilación, lint y pruebas. |
| `scripts/Cargar-Demo.ps1` | Carga opcional de ejemplos a través de la API sin repetir empresas existentes. |
| `scripts/datos/proveedores-demo.json` | Datos ficticios que consume el cargador. |
| `scripts/common/Entorno-Java.ps1` | Funciones compartidas para preparar/restaurar el entorno Java de los scripts. |
| `scripts/common/Api-Json.ps1` | Envío/lectura UTF-8 de JSON para el cargador, compatible con Windows PowerShell. |

`build/`, `.gradle/`, `.kotlin/` y `.tmp/` contienen productos generados, cachés o comprobaciones locales; no son capas del proyecto ni se usan para escribir funcionalidades. `.idea/` guarda preferencias del IDE. `.local/` solo puede contener el respaldo local antiguo mencionado en credenciales; ya no configura el backend.

### Recorrido de una operación: Guardar proveedor

1. `ModalProveedor` recibe el clic de Guardar y llama al callback recibido.
2. `AccionesProveedores`, conectado en `ProveedoresApp`, dirige la acción a `ProveedoresViewModel.guardar()`.
3. El ViewModel normaliza, llama a `ValidarProveedor.errores()` y revisa duplicados en los datos cargados. Si falla, actualiza errores y mensaje del modal.
4. Si es válido, utiliza `ProveedorRepository.agregar()` o `actualizar()`. La implementación `ApiProveedorRepository` convierte el DTO con Kotlin Serialization y utiliza `ClienteHttp`.
5. `ProveedorController` recibe JSON, valida `ProveedorRequest` y llama a `ProveedorService`. El servidor vuelve a comprobar reglas: no confía únicamente en Android.
6. El servicio usa el mapper y el repositorio JPA; PostgreSQL guarda el registro dentro de una transacción y aplica su restricción de unicidad.
7. La respuesta vuelve por el mismo camino. El ViewModel actualiza su lista con el resultado confirmado, cierra el formulario y muestra éxito; Compose vuelve a dibujar a partir del estado. Ante un error, se muestra el mensaje correspondiente.

Para cambiar únicamente el diseño de Guardar, editar el modal; para cambiar qué ocurre al pulsarlo, editar el ViewModel; para cambiar una regla obligatoria para cualquier cliente, editar el backend y mantener coherente la validación móvil.

### Ejemplos de cambios frecuentes

| Quiero… | Archivos que debo revisar |
|---|---|
| Cambiar el verde de botones | `ui/theme/Color.kt` y su asignación `primary` en `ui/theme/Theme.kt`. |
| Aumentar el texto o separación de una tarjeta | `ui/components/TarjetaProveedor.kt`; para estilos globales, `ui/theme/Type.kt`. |
| Añadir una opción de rubro | `domain/model/RubroProveedor.kt`. Si se requiere un catálogo obligatorio también en la API, añadir y validar esa regla en el backend. |
| Mostrar otro dato al expandir | `ui/components/DetalleProveedor.kt` y `DatoContacto.kt`; comprobar que el modelo y la API ya lo proporcionen. |
| Cambiar número de filas o su orden | `viewmodel/ProveedoresState.kt`; para apariencia de controles, `ui/components/PaginacionProveedores.kt`. |
| Cambiar cuándo aparece “Proveedor no encontrado” | `viewmodel/ProveedoresViewModel.kt`, método `buscarConfirmado()`; para el estado vacío en pantalla, `EstadoDirectorio.kt`. |
| Cambiar reglas del teléfono | `domain/validation/ValidarProveedor.kt`, límites en `domain/model/Campo.kt` y validaciones de `dto/ProveedorRequest.java` en el backend. |
| Añadir un campo persistente, por ejemplo RUC | Nueva migración SQL + entidad Java + DTO de entrada/salida + mapper; después modelo Kotlin + DTO `GuardarProveedorRequest` + `Campo`/`CamposProveedor` + validaciones + formulario y detalle. La biblioteca genera la conversión JSON. Actualizar contratos y pruebas en ambos lados. |
| Añadir una operación de negocio | Servicio/controlador/repositorio Java según corresponda; contrato de repositorio móvil, implementación HTTP, ViewModel, callback y componente que la activa. |

### Dónde están las pruebas

| Ruta | Qué comprueba |
|---|---|
| `app/src/test/java/com/example/contactoproveedores/ValidarProveedorTest.kt` | Reglas del formulario. |
| `app/src/test/java/com/example/contactoproveedores/PaginacionStateTest.kt` | Filtrado, orden y distribución por páginas en el estado. |
| `app/src/test/java/com/example/contactoproveedores/SerializacionProveedorTest.kt` | Contrato JSON: campos editables, tildes/comillas, lista, estado y mensaje de error. |
| `app/src/androidTest/java/com/example/contactoproveedores/ProveedorFlujoTest.kt` | Flujo de registro/edición/estado y búsqueda en emulador con API activa. |
| `app/src/androidTest/java/com/example/contactoproveedores/PanelAdaptativoTest.kt` | Menú y presentación de tabla en ancho suficiente. |
| `app/src/androidTest/java/com/example/contactoproveedores/TarjetaProveedorTest.kt` | Expansión y acciones de tarjeta. |
| `app/src/androidTest/java/com/example/contactoproveedores/PaginacionProveedoresTest.kt` | Interacción y límites de los controles de páginas. |
| `app/src/androidTest/java/com/example/contactoproveedores/testutil/CapturaPantalla.kt` | Utilidad de capturas para pruebas de interfaz. |
| `backend/src/test/java/com/example/proveedores/ProveedorApiTest.java` | API, validación, duplicados, edición y estado con base H2 aislada. |

Al cambiar reglas, contratos o comportamiento, actualizar las pruebas correspondientes. Mantener las consultas y credenciales en el backend, la lógica de interacción en el ViewModel y los componentes dedicados a presentar datos y emitir acciones.

### Conversión JSON automática

En Android se utiliza [Kotlin Serialization](https://kotlinlang.org/docs/serialization.html), con biblioteca JSON **1.9.0** y plugin de compilación alineado con Kotlin **2.2.10**. `@Serializable` hace que el compilador genere el código de conversión; no se escribe un serializador propio. `encodeToString()` transforma objetos en JSON y `decodeFromString()` transforma JSON en objetos o listas. La versión se declara en `gradle/libs.versions.toml` y el plugin/dependencia se activan en los archivos Gradle.

`Proveedor.kt` representa el registro leído. `GuardarProveedorRequest.kt` expresa los seis campos editables de una petición: ese DTO evita enviar ID o estado al guardar datos. La construcción del DTO selecciona datos, pero no escribe JSON campo por campo. Los cambios de estado envían un mapa booleano convertido por la misma biblioteca. El repositorio tolera campos adicionales en las respuestas mediante `ignoreUnknownKeys`; las validaciones del formulario y del servidor siguen siendo necesarias.

El antiguo `ProveedorJson.kt` se eliminó. En Java, Spring Boot Web usa Jackson para convertir automáticamente las peticiones y respuestas del controlador. Android y el backend utilizan bibliotecas distintas y comparten el mismo contrato HTTP/JSON.

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

El archivo `scripts/datos/proveedores-demo.json` contiene empresas, contactos, teléfonos y direcciones **inventados**, con presentación realista de Lima y Piura. No representan relaciones comerciales ni datos de contacto verificados; cualquier coincidencia es casual. Los nombres visibles no llevan etiquetas “demo” o “demostración”. Los correos usan el dominio de ejemplo `example.com`. El script omite empresas que ya existen (también reconoce el antiguo sufijo `(demo)`); no modifica registros anteriores ni reinicia identificadores. Incluye dos proveedores inactivos para comprobar los filtros.

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
