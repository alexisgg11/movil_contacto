param([switch]$ConEmulador)
$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
$opcionesJavaAnteriores = $env:JAVA_TOOL_OPTIONS
$rutaTemporal = Join-Path $raizProyecto '.tmp'
New-Item -ItemType Directory -Force $rutaTemporal | Out-Null
$env:JAVA_TOOL_OPTIONS = "$opcionesJavaAnteriores -Djdk.net.unixdomain.tmpdir=`"$rutaTemporal`""
if (Test-Path 'C:\Program Files\Android\Android Studio\jbr') {
    $env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
}
try {
    & (Join-Path $raizProyecto 'gradlew.bat') -p $raizProyecto :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Falló la verificación Android.' }
    & (Join-Path $raizProyecto 'gradlew.bat') -p (Join-Path $raizProyecto 'backend') test --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Falló la verificación de la API.' }
    if ($ConEmulador) {
        & (Join-Path $raizProyecto 'gradlew.bat') -p $raizProyecto :app:connectedDebugAndroidTest --console=plain
        if ($LASTEXITCODE -ne 0) { throw 'Falló la prueba del emulador. Verifica que el servidor y el emulador estén encendidos.' }
    }
} finally { $env:JAVA_TOOL_OPTIONS = $opcionesJavaAnteriores }
