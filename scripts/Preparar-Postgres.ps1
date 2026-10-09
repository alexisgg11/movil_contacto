$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
$psql = 'C:\Program Files\PostgreSQL\18\bin\psql.exe'
if (!(Test-Path $psql)) { throw 'No se encontró PostgreSQL 18. Ajusta la ruta de psql en este script.' }
$secreto = Read-Host 'Contraseña del usuario postgres (no se mostrará)' -AsSecureString
$credencial = [System.Management.Automation.PSCredential]::new('postgres', $secreto)
$env:PGPASSWORD = $credencial.GetNetworkCredential().Password
try {
    $existe = & $psql -h 127.0.0.1 -U postgres -d postgres -w -tAc "SELECT count(*) FROM pg_database WHERE datname='contacto_proveedores';"
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo autenticar con PostgreSQL.' }
    $archivoSecreto = Join-Path $raizProyecto '.local\db-credential.xml'
    if ($existe.Trim() -eq '1') {
        if (Test-Path $archivoSecreto) { Write-Host 'La base y las credenciales locales ya están preparadas.'; exit }
        throw 'La base contacto_proveedores ya existe. No se modificó. Consulta antes de reutilizarla.'
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
    New-Item -ItemType Directory -Force (Split-Path $archivoSecreto) | Out-Null
    $credApp = [System.Management.Automation.PSCredential]::new('contacto_app', (ConvertTo-SecureString $claveApp -AsPlainText -Force))
    $credApp | Export-Clixml -LiteralPath $archivoSecreto
    Write-Host 'Base contacto_proveedores creada. Credenciales cifradas para tu usuario de Windows.'
} finally {
    Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
    $claveApp = $null
}
