param([switch]$ConEmulador)
$ErrorActionPreference = 'Stop'
$raizProyecto = Split-Path $PSScriptRoot -Parent
. (Join-Path $PSScriptRoot 'common\Entorno-Java.ps1')
$entornoAnterior = Initialize-EntornoJava -RaizProyecto $raizProyecto
try {
    & (Join-Path $raizProyecto 'gradlew.bat') -p $raizProyecto :app:assembleDebug :app:testDebugUnitTest :app:lintDebug --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Falló la verificación Android.' }
    & (Join-Path $raizProyecto 'gradlew.bat') -p (Join-Path $raizProyecto 'backend') test --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Falló la verificación de la API.' }
    if ($ConEmulador) {
        & (Join-Path $raizProyecto 'gradlew.bat') -p $raizProyecto :app:connectedDebugAndroidTest --console=plain
        if ($LASTEXITCODE -ne 0) { throw 'Falló la prueba del emulador. Verifica que el servidor y el emulador estén encendidos.' }
    }
} finally { Restore-EntornoJava -Anterior $entornoAnterior }
