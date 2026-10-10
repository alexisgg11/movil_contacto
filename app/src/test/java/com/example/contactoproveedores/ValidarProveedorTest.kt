package com.example.contactoproveedores
import com.example.contactoproveedores.domain.*
import org.junit.Assert.*
import org.junit.Test
class ValidarProveedorTest {
    private val valido = Proveedor(empresa = "Andina", contacto = "Ana", telefono = "+51 987654321",
        correo = "ana@andina.pe", direccion = "Lima", rubro = "Suministros")
    @Test fun aceptaDatosValidos() { assertTrue(ValidarProveedor.errores(valido).isEmpty()) }
    @Test fun rechazaFormularioVacio() { assertEquals(6, ValidarProveedor.errores(Proveedor()).size) }
    @Test fun rechazaTelefonoYCorreoInvalidos() {
        val errores = ValidarProveedor.errores(valido.copy(telefono = "abc", correo = "invalido"))
        assertTrue(errores.containsKey(Campo.TELEFONO))
        assertTrue(errores.containsKey(Campo.CORREO))
    }
    @Test fun rechazaLongitudesExcesivas() {
        assertTrue(ValidarProveedor.errores(valido.copy(empresa = "a".repeat(121))).containsKey(Campo.EMPRESA))
    }
    @Test fun telefonoDebeContenerDigitosReales() {
        listOf("-------", "( ) - -", "123456", "1234567890123456").forEach {
            assertTrue(ValidarProveedor.errores(valido.copy(telefono = it)).containsKey(Campo.TELEFONO))
        }
    }
    @Test fun rechazaTextosDemasiadoCortos() {
        assertTrue(ValidarProveedor.errores(valido.copy(empresa = "A")).containsKey(Campo.EMPRESA))
        assertTrue(ValidarProveedor.errores(valido.copy(contacto = " ")).containsKey(Campo.CONTACTO))
    }
}
