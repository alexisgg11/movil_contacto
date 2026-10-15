package com.example.contactoproveedores

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.contactoproveedores.domain.model.Proveedor
import com.example.contactoproveedores.ui.components.TarjetaProveedor
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TarjetaProveedorTest {
    @get:Rule val compose = createComposeRule()
    @Test fun detalleSeRestauraYAccionesNoSeMezclan() {
        var ediciones = 0
        var estados = 0
        val restauracion = StateRestorationTester(compose)
        restauracion.setContent {
            MaterialTheme {
                TarjetaProveedor(Proveedor(id = 42, empresa = "Proveedor de prueba", contacto = "Ana",
                    telefono = "987654321", correo = "ana@ejemplo.pe", direccion = "Lima", rubro = "Servicios"),
                    false, { ediciones++ }, { estados++ })
            }
        }
        compose.onNodeWithText("987654321").assertDoesNotExist()
        compose.onNodeWithText("Proveedor de prueba").performClick()
        compose.onNodeWithText("987654321").assertIsDisplayed()
        restauracion.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("987654321").assertIsDisplayed()
        compose.onNodeWithText("Proveedor de prueba").performClick()
        compose.onNodeWithText("987654321").assertDoesNotExist()
        compose.runOnIdle { assertEquals(0, ediciones); assertEquals(0, estados) }
        compose.onNodeWithContentDescription("Editar proveedor").performClick()
        compose.onNodeWithContentDescription("Desactivar proveedor").performClick()
        compose.runOnIdle { assertEquals(1, ediciones); assertEquals(1, estados) }
        compose.onNodeWithText("987654321").assertDoesNotExist()
        compose.onNodeWithText("Ver detalle").assertDoesNotExist()
        compose.onNodeWithContentDescription("Expandir detalle").performClick()
        compose.onNodeWithText("987654321").assertIsDisplayed()
        compose.onNodeWithContentDescription("Contraer detalle").assertExists()
        compose.onNodeWithContentDescription("Editar proveedor").performClick()
        compose.onNodeWithContentDescription("Desactivar proveedor").performClick()
        compose.runOnIdle { assertEquals(2, ediciones); assertEquals(2, estados) }
        compose.onNodeWithText("987654321").assertIsDisplayed()
        compose.onNodeWithContentDescription("Contraer detalle").performClick()
        compose.onNodeWithText("987654321").assertDoesNotExist()
    }
}
