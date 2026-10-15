param([string]$ApiUrl = 'http://localhost:8080/api/proveedores')
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'common\Api-Json.ps1')
$ejemplos = Get-Content (Join-Path $PSScriptRoot 'datos\proveedores-demo.json') -Raw -Encoding UTF8 | ConvertFrom-Json
$existentes = Invoke-ApiJson -Uri $ApiUrl
foreach ($ejemplo in $ejemplos) {
    if ($existentes | Where-Object { $_.empresa -eq $ejemplo.empresa }) {
        Write-Host "Ya existe: $($ejemplo.empresa). No se modifico."
        continue
    }
    $cuerpo = $ejemplo | Select-Object empresa,contacto,telefono,correo,direccion,rubro | ConvertTo-Json -Compress
    $nuevo = Invoke-ApiJson -Method Post -Uri $ApiUrl -Body $cuerpo
    if (!$ejemplo.activo) {
        Invoke-ApiJson -Method Put -Uri "$ApiUrl/$($nuevo.id)/estado" -Body '{"activo":false}' | Out-Null
    }
    Write-Host "Creado: $($ejemplo.empresa)"
}
Write-Host 'Carga de datos ficticios terminada. No se borraron ni renumeraron registros existentes.'
