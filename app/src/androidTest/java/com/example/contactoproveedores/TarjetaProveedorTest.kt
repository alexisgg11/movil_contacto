package com.example.contactoproveedores

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.contactoproveedores.domain.Proveedor
import com.example.contactoproveedores.ui.components.TarjetaProveedor
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TarjetaProveedorTest {
    @get:Rule val compose = createComposeRule()

    @Test fun detalleSeExpandeSeRestauraYSeOcultaSinEliminar() {
        var eliminaciones = 0
        val restauracion = StateRestorationTester(compose)
        restauracion.setContent {
            MaterialTheme {
                TarjetaProveedor(
                    Proveedor(id = 42, empresa = "Proveedor de prueba", contacto = "Ana",
                        telefono = "987654321", correo = "ana@ejemplo.pe",
                        direccion = "Av. Principal 123", rubro = "Servicios"),
                    ocupado = false, eliminar = { eliminaciones++ }
                )
            }
        }
        compose.onNodeWithText("Proveedor de prueba").assertIsDisplayed()
        compose.onNodeWithText("987654321").assertDoesNotExist()
        compose.onNodeWithText("Ver detalle").performClick()
        compose.onNodeWithText("987654321").assertIsDisplayed()
        compose.onNodeWithText("ana@ejemplo.pe").assertIsDisplayed()
        compose.onNodeWithText("Av. Principal 123").assertIsDisplayed()
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Ocultar detalle").assertIsDisplayed().performClick()
        compose.onNodeWithText("987654321").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, eliminaciones) }
        compose.onNodeWithContentDescription("Eliminar proveedor").performClick()
        compose.runOnIdle { assertEquals(1, eliminaciones) }
    }
}
