$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
$archivoEnv = Join-Path $raizProyecto '.env'
if (!(Test-Path -LiteralPath $archivoEnv) -and !($env:DB_USER -and $env:DB_PASSWORD)) {
    throw 'Copia .env.example como .env y completa tus credenciales, o ejecuta Preparar-Postgres.ps1 para una instalacion nueva.'
}
. (Join-Path $PSScriptRoot 'common\Entorno-Java.ps1')
$entornoAnterior = Initialize-EntornoJava -RaizProyecto $raizProyecto
try {
    & (Join-Path $raizProyecto 'gradlew.bat') -p (Join-Path $raizProyecto 'backend') run --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'El servidor no pudo iniciar. Revisa .env y el servicio PostgreSQL.' }
} finally { Restore-EntornoJava -Anterior $entornoAnterior }
