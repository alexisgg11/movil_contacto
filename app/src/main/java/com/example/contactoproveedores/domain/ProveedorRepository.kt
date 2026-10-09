package com.example.contactoproveedores.domain
interface ProveedorRepository {
    suspend fun listar(): List<Proveedor>
    suspend fun agregar(proveedor: Proveedor): Proveedor
    suspend fun eliminar(id: Long)
}
