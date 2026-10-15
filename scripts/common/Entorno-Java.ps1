function Initialize-EntornoJava {
    param([Parameter(Mandatory)][string]$RaizProyecto)
    $anterior = @{ JAVA_HOME = $env:JAVA_HOME; JAVA_TOOL_OPTIONS = $env:JAVA_TOOL_OPTIONS }
    $temporal = Join-Path $RaizProyecto '.tmp'
    New-Item -ItemType Directory -Force $temporal | Out-Null
    $env:JAVA_TOOL_OPTIONS = "$($anterior.JAVA_TOOL_OPTIONS) -Djdk.net.unixdomain.tmpdir=`"$temporal`""
    $jbr = 'C:\Program Files\Android\Android Studio\jbr'
    if (Test-Path $jbr) { $env:JAVA_HOME = $jbr }
    return $anterior
}
function Restore-EntornoJava {
    param([Parameter(Mandatory)][hashtable]$Anterior)
    $env:JAVA_HOME = $Anterior.JAVA_HOME
    $env:JAVA_TOOL_OPTIONS = $Anterior.JAVA_TOOL_OPTIONS
}
