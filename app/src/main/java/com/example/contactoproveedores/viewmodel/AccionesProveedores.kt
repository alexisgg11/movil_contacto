package com.example.contactoproveedores.viewmodel

import com.example.contactoproveedores.domain.model.*

data class AccionesProveedores(
    val nuevo: () -> Unit,
    val abrirEdicion: (Proveedor) -> Unit,
    val cerrarFormulario: () -> Unit,
    val editarCampo: (Campo, String) -> Unit,
    val guardar: () -> Unit,
    val buscar: (String) -> Unit,
    val buscarConfirmado: () -> Unit,
    val filtrar: (FiltroEstado) -> Unit,
    val confirmarEstado: (Proveedor?) -> Unit,
    val cambiarEstado: () -> Unit,
    val recargar: () -> Unit,
    val cerrarModal: () -> Unit
)
