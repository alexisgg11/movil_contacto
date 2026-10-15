package com.example.contactoproveedores.data.repository

import com.example.contactoproveedores.data.remote.ClienteHttp
import com.example.contactoproveedores.data.remote.dto.GuardarProveedorRequest
import com.example.contactoproveedores.domain.model.Proveedor
import com.example.contactoproveedores.domain.repository.ProveedorRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ApiProveedorRepository(private val http: ClienteHttp) : ProveedorRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun listar(): List<Proveedor> =
        json.decodeFromString(http.solicitar("GET", ""))

    override suspend fun agregar(proveedor: Proveedor): Proveedor =
        json.decodeFromString(http.solicitar("POST", "", json.encodeToString(GuardarProveedorRequest(proveedor))))

    override suspend fun actualizar(proveedor: Proveedor): Proveedor =
        json.decodeFromString(http.solicitar("PUT", "/${proveedor.id}", json.encodeToString(GuardarProveedorRequest(proveedor))))

    override suspend fun cambiarEstado(id: Long, activo: Boolean): Proveedor =
        json.decodeFromString(http.solicitar("PUT", "/$id/estado", json.encodeToString(mapOf("activo" to activo))))
}
