package com.example.contactoproveedores.data.remote.dto

import com.example.contactoproveedores.domain.model.Proveedor
import kotlinx.serialization.Serializable

/** Datos editables aceptados por POST y PUT; el servidor controla ID y estado. */
@Serializable
data class GuardarProveedorRequest(
    val empresa: String,
    val contacto: String,
    val telefono: String,
    val correo: String,
    val direccion: String,
    val rubro: String
) {
    constructor(p: Proveedor) : this(p.empresa, p.contacto, p.telefono, p.correo, p.direccion, p.rubro)
}
