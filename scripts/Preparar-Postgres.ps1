$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
$archivoEnv = Join-Path $raizProyecto '.env'
if (Test-Path -LiteralPath $archivoEnv) {
    Write-Host 'Ya existe .env. No se modificaron archivos ni bases. Usa esa configuracion o revisala antes de preparar otra instalacion.'
    return
}
$psql = 'C:\Program Files\PostgreSQL\18\bin\psql.exe'
if (!(Test-Path $psql)) { throw 'No se encontró PostgreSQL 18. Ajusta la ruta de psql en este script.' }
$secreto = Read-Host 'Contraseña del usuario postgres (no se mostrará)' -AsSecureString
$credencial = [System.Management.Automation.PSCredential]::new('postgres', $secreto)
$env:PGPASSWORD = $credencial.GetNetworkCredential().Password
try {
    $existe = & $psql -h 127.0.0.1 -U postgres -d postgres -w -tAc "SELECT count(*) FROM pg_database WHERE datname='contacto_proveedores';"
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo autenticar con PostgreSQL.' }
    if ($existe.Trim() -eq '1') {
        if (Test-Path $archivoEnv) { Write-Host 'La base y .env ya estan preparados. No se modificaron.'; exit }
        throw 'La base contacto_proveedores ya existe. No se modificó. Configura .env con sus credenciales antes de reutilizarla.'
    }
    $rolExiste = & $psql -h 127.0.0.1 -U postgres -d postgres -w -tAc "SELECT count(*) FROM pg_roles WHERE rolname='contacto_app';"
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo comprobar el rol.' }
    if ($rolExiste.Trim() -ne '0') { throw 'El rol contacto_app ya existe. No se modificó.' }
    $bytes = New-Object byte[] 32
    $generador = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $generador.GetBytes($bytes)
    $generador.Dispose()
    $claveApp = -join ($bytes | ForEach-Object { $_.ToString('x2') })
    "CREATE ROLE contacto_app LOGIN PASSWORD '$claveApp';" | & $psql -h 127.0.0.1 -U postgres -d postgres -w -v ON_ERROR_STOP=1
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear el rol.' }
    & $psql -h 127.0.0.1 -U postgres -d postgres -w -v ON_ERROR_STOP=1 -c 'CREATE DATABASE contacto_proveedores OWNER contacto_app;'
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo crear la base. El rol contacto_app quedó creado.' }
    if (Test-Path -LiteralPath $archivoEnv) { throw 'Ya existe .env. No se sobrescribio; configura los datos del usuario creado.' }
    $lineasEnv = @('# Configuracion local. No subir a GitHub.',
        'DB_URL=jdbc:postgresql://127.0.0.1:5432/contacto_proveedores',
        'DB_USER=contacto_app', ('DB_PASSWORD=' + $claveApp), 'PORT=8080')
    [System.IO.File]::WriteAllLines($archivoEnv, $lineasEnv, [System.Text.UTF8Encoding]::new($false))
    $lineasEnv = $null
    Write-Host 'Base contacto_proveedores creada. Credenciales guardadas en .env (no subir a GitHub).'
} finally {
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
    $claveApp = $null
}
