function Invoke-ApiJson {
    param([Parameter(Mandatory)][string]$Uri, [string]$Method = 'Get', [string]$Body)
    $parametros = @{ Uri = $Uri; Method = $Method; UseBasicParsing = $true; ErrorAction = 'Stop' }
    if ($Body) {
        $parametros.ContentType = 'application/json; charset=utf-8'
        $parametros.Body = [System.Text.Encoding]::UTF8.GetBytes($Body)
    }
    $respuesta = Invoke-WebRequest @parametros
    $respuesta.RawContentStream.Position = 0
    $lector = [System.IO.StreamReader]::new($respuesta.RawContentStream, [System.Text.Encoding]::UTF8)
    try {
        $contenido = $lector.ReadToEnd()
        if ($contenido) { return ConvertFrom-Json -InputObject $contenido }
    } finally { $lector.Dispose() }
}
