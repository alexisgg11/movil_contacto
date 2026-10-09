package com.example.contactoproveedores.data
import com.example.contactoproveedores.domain.Proveedor
import com.example.contactoproveedores.domain.ProveedorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ApiProveedorRepository(private val baseUrl: String) : ProveedorRepository {
    override suspend fun listar(): List<Proveedor> {
        val array = JSONArray(solicitar("GET"))
        return List(array.length()) { ProveedorJson.leer(array.getJSONObject(it)) }
    }
    override suspend fun agregar(proveedor: Proveedor) =
        ProveedorJson.leer(JSONObject(solicitar("POST", cuerpo = ProveedorJson.escribir(proveedor))))
    override suspend fun eliminar(id: Long) { solicitar("DELETE", "/$id") }
    private suspend fun solicitar(metodo: String, ruta: String = "", cuerpo: String? = null): String =
        withContext(Dispatchers.IO) {
            val conexion = URL("${baseUrl.trimEnd('/')}/api/proveedores$ruta").openConnection() as HttpURLConnection
            try {
                conexion.requestMethod = metodo
                conexion.connectTimeout = 8000
                conexion.readTimeout = 8000
                conexion.setRequestProperty("Accept", "application/json")
                if (cuerpo != null) {
                    conexion.doOutput = true
                    conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    conexion.outputStream.use { it.write(cuerpo.toByteArray(Charsets.UTF_8)) }
                }
                val codigo = conexion.responseCode
                if (codigo !in 200..299) {
                    val detalle = conexion.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    val mensaje = runCatching { JSONObject(detalle ?: "{}").optString("message") }.getOrNull()
                    throw ApiException(mensaje?.takeIf { it.isNotBlank() } ?: "El servidor respondió con error $codigo")
                }
                if (codigo == 204) "" else conexion.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } finally { conexion.disconnect() }
        }
}
class ApiException(message: String) : Exception(message)
