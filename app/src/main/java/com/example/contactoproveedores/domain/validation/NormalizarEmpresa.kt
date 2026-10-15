package com.example.contactoproveedores.domain.validation

object NormalizarEmpresa {
    fun clave(texto: String) = texto.trim().replace(Regex("\\s+"), " ").lowercase(java.util.Locale.ROOT)
}
