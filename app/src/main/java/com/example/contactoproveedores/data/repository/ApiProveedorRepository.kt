package com.example.contactoproveedores.data.repository

import com.example.contactoproveedores.data.remote.ClienteHttp
import com.example.contactoproveedores.data.remote.ProveedorJson
import com.example.contactoproveedores.domain.model.Proveedor
import com.example.contactoproveedores.domain.repository.ProveedorRepository
import org.json.JSONArray
import org.json.JSONObject

class ApiProveedorRepository(private val http: ClienteHttp) : ProveedorRepository {
    override suspend fun listar(): List<Proveedor> {
        val array = JSONArray(http.solicitar("GET", ""))
        return List(array.length()) { ProveedorJson.leer(array.getJSONObject(it)) }
    }
    override suspend fun agregar(proveedor: Proveedor) =
        ProveedorJson.leer(JSONObject(http.solicitar("POST", "", ProveedorJson.escribir(proveedor))))
    override suspend fun actualizar(proveedor: Proveedor) =
        ProveedorJson.leer(JSONObject(http.solicitar("PUT", "/${proveedor.id}", ProveedorJson.escribir(proveedor))))
    override suspend fun cambiarEstado(id: Long, activo: Boolean) =
        ProveedorJson.leer(JSONObject(http.solicitar("PUT", "/$id/estado", ProveedorJson.estado(activo))))
}
