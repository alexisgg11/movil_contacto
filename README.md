# Contacto · Panel de proveedores

Proyecto del curso **Tecnología y Desarrollo Móvil**. Aplicación Android nativa en **Kotlin y Jetpack Compose**, API independiente en **Java y Spring Boot**, y persistencia en **PostgreSQL**.

## Qué incluye

- Menú con **Resumen** y **Proveedores**.
- Panel con total de proveedores, activos e inactivos.
- Registro y edición en un modal con seis campos: empresa, contacto, teléfono, correo, dirección y rubro.
- Búsqueda y filtros por estado.
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

### 3. Preparar PostgreSQL: una sola vez por equipo

Comprueba que el servicio PostgreSQL esté iniciado en **Servicios** de Windows. En PowerShell, desde la raíz:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Preparar-Postgres.ps1
```

Escribe la contraseña de **postgres** elegida al instalar PostgreSQL. La entrada es oculta; no aparecen caracteres mientras escribes.

El script crea la base `contacto_proveedores`, el usuario `contacto_app` y una contraseña aleatoria cifrada en `.local/db-credential.xml`. Espera el mensaje **«Base contacto_proveedores creada»**.

La ruta predeterminada de `psql.exe` es `C:\Program Files\PostgreSQL\18\bin\psql.exe`. Si tu instalación es distinta, ajusta `$psql` en el script.

No crees tablas ni importes respaldos manualmente. Flyway aplica las migraciones al iniciar el servidor. Cada compañero comienza con su propia base vacía; clonar Git no copia contactos ni credenciales.

El script no sobrescribe una base o un usuario existentes sin la configuración local correspondiente. Si encuentra esos nombres, revisa la instalación antes de reutilizarlos; no borres bases sin revisar su contenido.

### 4. Iniciar Spring Boot

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\Iniciar-Backend.ps1
```

Espera **`Tomcat started on port 8080`** y **`Started ProveedoresApplication`**. La primera ejecución descarga dependencias.

Mantén abierta la terminal; `Ctrl+C` detiene el servidor. Este comando inicia Spring Boot, no PostgreSQL ni Android.

Comprueba en el navegador de la PC:

```text
http://localhost:8080/api/proveedores
```

Una instalación sin registros devuelve `[]`.

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

La instalación automatizada las guarda en:

```text
<carpeta-del-proyecto>/.local/db-credential.xml
Ejemplo: C:\Android\contacto\.local\db-credential.xml
```

- Es una credencial cifrada con **Windows DPAPI**, vinculada al usuario y equipo que la creó; no es un archivo de texto con contraseña legible.
- `.local/` está excluida por `.gitignore`. **Nunca subas este archivo a GitHub.**
- La lectura se centraliza en `scripts/common/Credenciales-Postgres.ps1`.
- El script de inicio pasa la contraseña mediante `DB_PASSWORD` y restaura las variables al terminar.
- `application.properties` contiene `${DB_PASSWORD}`, no una contraseña real.
- `DB_USER` también se recibe por variable de entorno; el script lo obtiene de la credencial cifrada. `DB_URL` permite cambiar la conexión y tiene una dirección local predeterminada sin secretos.
- Android solo conoce la URL de la API; no recibe credenciales PostgreSQL.
- Cada compañero debe ejecutar la preparación en su máquina. No copies credenciales cifradas de otro usuario/equipo.

Para una base administrada manualmente, configura `DB_URL`, `DB_USER`, `DB_PASSWORD` y ejecuta `.\gradlew.bat -p backend run`. En Linux/macOS prepara la base y las variables manualmente y usa `./gradlew -p backend run`; los scripts automáticos son para Windows.

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
  common/                            Java y lectura de credenciales compartidos
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

La búsqueda y los filtros Android operan sobre la lista cargada. En la API la búsqueda se ejecuta en la consulta de base de datos. Para directorios grandes, el siguiente paso es paginación y consultas remotas desde Android; actualmente la lista completa se descarga.

## Migraciones y datos existentes

Flyway conserva el esquema versionado. V3 marca como activos los registros anteriores y añade una clave de empresa normalizada indexada para comprobar duplicados sin cargar toda la tabla. PostgreSQL impide duplicados incluso con solicitudes simultáneas.

Si una instalación anterior contiene duplicados, revisa esos datos antes de actualizar; las migraciones no borran ni fusionan registros automáticamente. `ddl-auto=validate` verifica el esquema y no recrea la base al iniciar.

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
| Credencial no se puede descifrar | Solo funciona en el usuario y equipo que la creó; prepara las credenciales propias. |
| Puerto 8080 ocupado | No inicies dos instancias del backend. |
| SDK/Gradle incompatible | Android Studio compatible con AGP 9.4.1, API 37 y JDK configurado. |
| Emulador `offline` o `Device is already activating` | No pulses Run repetidamente. Detén el dispositivo y usa **Cold Boot** en Device Manager. |
| Emulador se cierra inesperadamente | Prueba arranque en frío y gráficos **Software** en su configuración. Evita Wipe Data si necesitas conservar datos locales. |
| Error de ruta temporal Java | Usa una ruta corta y los scripts del proyecto. |
| No aparecen datos de otra PC | GitHub comparte código; cada instalación tiene su propia base. |
| Empresa duplicada pero no aparece | Selecciona **Todos**: puede estar inactiva. Edítala o reactívala. |
