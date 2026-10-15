function Get-CredencialPostgres {
    param([Parameter(Mandatory)][string]$RaizProyecto)
    $archivo = Join-Path $RaizProyecto '.local\db-credential.xml'
    if (!(Test-Path -LiteralPath $archivo)) {
        throw 'Primero ejecuta scripts\Preparar-Postgres.ps1 con tu usuario de Windows.'
    }
    try { return Import-Clixml -LiteralPath $archivo }
    catch { throw 'No se pudo descifrar la credencial local. Debe usarse en el mismo equipo y usuario de Windows que la creó.' }
}
