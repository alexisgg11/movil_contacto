package com.example.contactoproveedores.domain.repository

import com.example.contactoproveedores.domain.model.Proveedor

interface ProveedorRepository {
    suspend fun listar(): List<Proveedor>
    suspend fun agregar(proveedor: Proveedor): Proveedor
    suspend fun actualizar(proveedor: Proveedor): Proveedor
    suspend fun cambiarEstado(id: Long, activo: Boolean): Proveedor
}
