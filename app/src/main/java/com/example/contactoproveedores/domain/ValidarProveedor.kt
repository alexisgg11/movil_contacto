package com.example.contactoproveedores.domain
object ValidarProveedor {
    fun errores(p: Proveedor): Map<Campo, String> = buildMap {
        Campo.entries.forEach { campo ->
            val texto = p.valor(campo).trim()
            if (texto.isBlank()) put(campo, "Completa este campo")
            else if (texto.length > campo.limite) put(campo, "Máximo ${campo.limite} caracteres")
        }
        if (p.telefono.isNotBlank() && !Regex("^\\+?[0-9 ()-]{7,20}$").matches(p.telefono.trim()))
            put(Campo.TELEFONO, "Ingresa un teléfono válido")
        if (p.correo.isNotBlank() && !Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(p.correo.trim()))
            put(Campo.CORREO, "Ingresa un correo válido")
    }
}
