package com.example.contactoproveedores.data.remote

import com.example.contactoproveedores.domain.repository.ErrorProveedor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL

class ClienteHttp(private val baseUrl: String) {
    suspend fun solicitar(metodo: String, ruta: String, cuerpo: String? = null): String =
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
                    val mensaje = runCatching {
                        Json.decodeFromString<Map<String, String>>(detalle ?: "{}")["message"]
                    }.getOrNull()
                    throw ErrorProveedor(mensaje?.takeIf { it.isNotBlank() }
                        ?: "El servidor respondió con error $codigo", codigo)
                }
                if (codigo == 204) "" else conexion.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } finally { conexion.disconnect() }
        }
}
