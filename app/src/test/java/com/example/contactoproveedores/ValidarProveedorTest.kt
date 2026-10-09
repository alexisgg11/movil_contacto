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
}
