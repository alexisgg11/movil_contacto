$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
. (Join-Path $PSScriptRoot 'common\Entorno-Java.ps1')
. (Join-Path $PSScriptRoot 'common\Credenciales-Postgres.ps1')
$credencial = Get-CredencialPostgres -RaizProyecto $raizProyecto
$entornoAnterior = Initialize-EntornoJava -RaizProyecto $raizProyecto
$conexionAnterior = @{ Usuario = $env:DB_USER; Clave = $env:DB_PASSWORD; Url = $env:DB_URL }
$env:DB_USER = $credencial.UserName
$env:DB_PASSWORD = $credencial.GetNetworkCredential().Password
if (!$env:DB_URL) { $env:DB_URL = 'jdbc:postgresql://127.0.0.1:5432/contacto_proveedores' }
try {
    & (Join-Path $raizProyecto 'gradlew.bat') -p (Join-Path $raizProyecto 'backend') run --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'El servidor no pudo iniciar.' }
} finally {
    $env:DB_USER = $conexionAnterior.Usuario
    $env:DB_PASSWORD = $conexionAnterior.Clave
    $env:DB_URL = $conexionAnterior.Url
    Restore-EntornoJava -Anterior $entornoAnterior
}
