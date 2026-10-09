package com.example.contactoproveedores

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import org.junit.Rule
import org.junit.Test

/** Requiere la API local encendida; crea y elimina su propio proveedor de prueba. */
class ProveedorFlujoTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun registrarBuscarReabrirYEliminar() {
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission("com.example.contactoproveedores", "android.permission.ACCESS_LOCAL_NETWORK")
            compose.activityRule.scenario.recreate()
        }
        val empresa = "Proveedor UI " + System.nanoTime()
        compose.waitUntil(15000) {
            compose.onAllNodesWithText("+  Agregar proveedor").fetchSemanticsNodes().singleOrNull()?.config?.contains(SemanticsProperties.Disabled) == false
        }
        val campos = mapOf("Empresa" to empresa, "Persona de contacto" to "Ana Torres",
            "Teléfono" to "987654321", "Correo electrónico" to "ana@andina.pe",
            "Dirección" to "Av. Primavera 120, Lima", "Rubro" to "Suministros")
        campos.forEach { (etiqueta, texto) ->
            compose.onNode(hasSetTextAction() and hasText(etiqueta)).performScrollTo().performTextInput(texto)
        }
        compose.onNodeWithText("+  Agregar proveedor").performScrollTo().performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(2)
        compose.waitUntil(15000) { compose.onAllNodesWithText("Proveedor guardado").fetchSemanticsNodes().isNotEmpty() }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(15000) {
            compose.onAllNodesWithText("+  Agregar proveedor").fetchSemanticsNodes().singleOrNull()?.config?.contains(SemanticsProperties.Disabled) == false
        }
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(2)
        compose.onNode(hasSetTextAction() and hasText("Buscar proveedores"))
            .performScrollTo().performTextInput(empresa)
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(3)
        compose.onNode(hasText(empresa) and hasSetTextAction().not()).performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Eliminar proveedor").performScrollTo().performClick()
        compose.onNodeWithText("¿Eliminar proveedor?").assertIsDisplayed()
        compose.onAllNodesWithText("Eliminar").onLast().performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToIndex(2)
        compose.waitUntil(15000) { compose.onAllNodesWithText("Proveedor eliminado").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Sin coincidencias").performScrollTo().assertIsDisplayed()
    }
}
