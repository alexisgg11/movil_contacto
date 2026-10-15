package com.example.contactoproveedores.viewmodel

import com.example.contactoproveedores.domain.model.*

data class ProveedoresState(
    val formulario: Proveedor = Proveedor(),
    val formularioAbierto: Boolean = false,
    val proveedores: List<Proveedor> = emptyList(),
    val errores: Map<Campo, String> = emptyMap(),
    val busqueda: String = "",
    val filtro: FiltroEstado = FiltroEstado.TODOS,
    val pagina: Int = 0,
    val tamanioPagina: Int = 5,
    val ocupado: Boolean = false,
    val cargado: Boolean = false,
    val error: String? = null,
    val aviso: String? = null,
    val cambioEstado: Proveedor? = null,
    val modal: MensajeModal? = null
) {
    val activos get() = proveedores.count { it.activo }
    val inactivos get() = proveedores.size - activos
    val resultados get() = proveedores.filter { p ->
        filtro.acepta(p) && listOf(p.empresa, p.contacto, p.telefono, p.correo, p.direccion, p.rubro)
            .any { it.contains(busqueda.trim(), ignoreCase = true) }
    }.sortedBy { it.id }
    val totalPaginas get() = maxOf(1, (resultados.size + tamanioPagina - 1) / tamanioPagina)
    val paginaActual get() = pagina.coerceIn(0, totalPaginas - 1)
    val proveedoresPagina get() = resultados.drop(paginaActual * tamanioPagina).take(tamanioPagina)
}
