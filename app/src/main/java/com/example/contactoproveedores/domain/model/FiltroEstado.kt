package com.example.contactoproveedores.domain.model

enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"), ACTIVOS("Activos"), INACTIVOS("Inactivos");
    fun acepta(proveedor: Proveedor) = when (this) {
        TODOS -> true
        ACTIVOS -> proveedor.activo
        INACTIVOS -> !proveedor.activo
    }
}
