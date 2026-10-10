package com.example.contactoproveedores.domain
object ValidarProveedor {
    fun errores(p: Proveedor): Map<Campo, String> = buildMap {
        Campo.entries.forEach { campo ->
            val texto = p.valor(campo).trim()
            if (texto.isBlank()) put(campo, "Completa este campo")
            else if (texto.length > campo.limite) put(campo, "Máximo ${campo.limite} caracteres")
            else if (campo != Campo.TELEFONO && campo != Campo.CORREO && texto.length < 2)
                put(campo, "Ingresa al menos 2 caracteres")
        }
        if (p.telefono.isNotBlank() && !Regex("^(?=(?:\\D*\\d){7,15}\\D*$)\\+?[0-9 ()-]{7,20}$").matches(p.telefono.trim()))
            put(Campo.TELEFONO, "Usa de 7 a 15 dígitos; puedes incluir +, espacios, guiones y paréntesis")
        if (p.correo.isNotBlank() && !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(p.correo.trim()))
            put(Campo.CORREO, "Ingresa un correo válido")
    }
}
