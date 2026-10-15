package com.example.contactoproveedores

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import org.junit.Rule
import org.junit.Test
import com.example.contactoproveedores.testutil.CapturaPantalla

/** Requiere backend local. La prueba conserva el registro como inactivo. */
class ProveedorFlujoTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun captura(nombre: String) {
        CapturaPantalla.guardar(compose, nombre, if (nombre == "crm-formulario") "Nuevo proveedor" else null)
    }
    private fun aceptar(titulo: String) {
        compose.waitUntil(15000) { compose.onAllNodesWithText(titulo).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText(titulo).assertIsDisplayed()
        compose.onNodeWithText("Entendido").performClick()
    }
    @Test fun registrarEditarDesactivarReabrirYReactivar() {
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission("com.example.contactoproveedores", "android.permission.ACCESS_LOCAL_NETWORK")
            compose.activityRule.scenario.recreate()
        }
        compose.waitUntil(15000) {
            compose.onAllNodesWithText("+  Registrar proveedor").fetchSemanticsNodes().singleOrNull()
                ?.config?.contains(SemanticsProperties.Disabled) == false
        }
        val empresa = "Proveedor CRM UI " + System.nanoTime()
        compose.onNodeWithText("+  Registrar proveedor").performScrollTo().performClick()
        captura("crm-formulario")
        compose.onNodeWithText("Guardar").performClick()
        aceptar("Revisa el formulario")
        val campos = mapOf("Empresa" to empresa, "Persona de contacto" to "Ana Torres",
            "Teléfono" to "987654321", "Correo electrónico" to "ana@andina.pe",
            "Dirección" to "Av. Primavera 120, Lima", "Rubro" to "Suministros")
        campos.forEach { (etiqueta, texto) ->
            compose.onNode(hasSetTextAction() and hasText(etiqueta)).performScrollTo().performTextInput(texto)
        }
        compose.onNodeWithText("Guardar").performClick()
        aceptar("Registro exitoso")
        compose.onNode(hasSetTextAction() and hasText("Buscar proveedores")).performScrollTo().performTextInput(empresa)
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        captura("crm-panel")
        compose.onNodeWithContentDescription("Editar proveedor").performScrollTo().performClick()
        compose.onNode(hasSetTextAction() and hasText("Persona de contacto")).performScrollTo().performTextReplacement("Ana Actualizada")
        compose.onNodeWithText("Guardar").performClick()
        aceptar("Cambios guardados")
        compose.onNodeWithText("Ana Actualizada").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Desactivar proveedor").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithContentDescription("Desactivar proveedor").performScrollTo().performClick()
        compose.onNodeWithText("Desactivar").performClick()
        aceptar("Estado actualizado")
        compose.onNode(hasText("Inactivos") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithContentDescription("Reactivar proveedor").performScrollTo().assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15000) { compose.onAllNodesWithContentDescription("Reactivar proveedor").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Reactivar proveedor").performScrollTo().performClick()
        compose.onNodeWithText("Reactivar").performClick()
        aceptar("Estado actualizado")
        compose.onNode(hasText("Activos") and hasClickAction()).performScrollTo().performClick()
        compose.onNodeWithContentDescription("Desactivar proveedor").performScrollTo().performClick()
        compose.onNodeWithText("Desactivar").performClick()
        aceptar("Estado actualizado")
        compose.onNode(hasSetTextAction() and hasText("Buscar proveedores")).performScrollTo().performTextReplacement("Sin coincidencias " + empresa)
        androidx.test.espresso.Espresso.closeSoftKeyboard()
        compose.onNodeWithText("Buscar", substring = false).performScrollTo().performClick()
        aceptar("Proveedor no encontrado")
    }
}
