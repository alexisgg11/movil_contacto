package com.example.contactoproveedores.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Proveedor(
    val id: Long = 0,
    val empresa: String = "",
    val contacto: String = "",
    val telefono: String = "",
    val correo: String = "",
    val direccion: String = "",
    val rubro: String = "",
    val activo: Boolean = true
) {
    fun normalizado() = copy(empresa = empresa.trim(), contacto = contacto.trim(),
        telefono = telefono.trim(), correo = correo.trim(), direccion = direccion.trim(), rubro = rubro.trim())
}
