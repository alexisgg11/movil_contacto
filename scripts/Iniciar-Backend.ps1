$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
$archivoSecreto = Join-Path $raizProyecto '.local\db-credential.xml'
if (!(Test-Path $archivoSecreto)) { throw 'Primero ejecuta scripts\Preparar-Postgres.ps1.' }
$cred = Import-Clixml -LiteralPath $archivoSecreto
$env:DB_USER = $cred.UserName
$env:DB_PASSWORD = $cred.GetNetworkCredential().Password
$env:DB_URL = 'jdbc:postgresql://127.0.0.1:5432/contacto_proveedores'
$opcionesJavaAnteriores = $env:JAVA_TOOL_OPTIONS
$rutaTemporal = Join-Path $raizProyecto '.tmp'
New-Item -ItemType Directory -Force $rutaTemporal | Out-Null
$env:JAVA_TOOL_OPTIONS = "$opcionesJavaAnteriores -Djdk.net.unixdomain.tmpdir=`"$rutaTemporal`""
if (Test-Path 'C:\Program Files\Android\Android Studio\jbr') {
    $env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
}
try {
    & (Join-Path $raizProyecto 'gradlew.bat') -p (Join-Path $raizProyecto 'backend') run --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'El servidor no pudo iniciar.' }
} finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    $env:JAVA_TOOL_OPTIONS = $opcionesJavaAnteriores
}
