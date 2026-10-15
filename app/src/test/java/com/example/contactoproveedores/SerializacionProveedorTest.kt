package com.example.contactoproveedores

import com.example.contactoproveedores.data.remote.dto.GuardarProveedorRequest
import com.example.contactoproveedores.domain.model.Proveedor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.*
import org.junit.Test

class SerializacionProveedorTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test fun solicitudEnviaSoloDatosEditablesYConservaCaracteresEspeciales() {
        val proveedor = Proveedor(id = 23, empresa = "Logística \"Andina\"", contacto = "José",
            telefono = "987654321", correo = "jose@example.com", direccion = "Av. Perú\nLima",
            rubro = "Transporte y logística", activo = false)
        val solicitud = GuardarProveedorRequest(proveedor)
        val texto = json.encodeToString(solicitud)
        assertEquals(setOf("empresa", "contacto", "telefono", "correo", "direccion", "rubro"),
            json.parseToJsonElement(texto).jsonObject.keys)
        assertEquals(solicitud, json.decodeFromString<GuardarProveedorRequest>(texto))
    }

    @Test fun respuestaLeeListaYEstadoEIgnoraCamposNuevosDelServidor() {
        val texto = """[{"id":11,"empresa":"Logística Andina","contacto":"José","telefono":"987654321","correo":"jose@example.com","direccion":"Lima","rubro":"Servicios","activo":false,"campoFuturo":"dato"}]"""
        val lista = json.decodeFromString<List<Proveedor>>(texto)
        assertEquals(1, lista.size)
        assertEquals(11L, lista.single().id)
        assertEquals("Logística Andina", lista.single().empresa)
        assertFalse(lista.single().activo)
        assertTrue(json.decodeFromString<List<Proveedor>>("[]").isEmpty())
    }

    @Test fun cambioEstadoMantieneBooleanoYErrorMantieneMensaje() {
        val texto = json.encodeToString(mapOf("activo" to false))
        assertEquals(mapOf("activo" to false), json.decodeFromString<Map<String, Boolean>>(texto))
        val error = Json.decodeFromString<Map<String, String>>("""{"message":"La empresa ya existe"}""")
        assertEquals("La empresa ya existe", error["message"])
    }
}
