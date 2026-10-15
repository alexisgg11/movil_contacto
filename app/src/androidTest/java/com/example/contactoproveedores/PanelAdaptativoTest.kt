package com.example.contactoproveedores

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.contactoproveedores.testutil.CapturaPantalla
import org.junit.Rule
import org.junit.Test

class PanelAdaptativoTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun menuFuncionaYTablaApareceEnPantallaAmplia() {
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission("com.example.contactoproveedores", "android.permission.ACCESS_LOCAL_NETWORK")
            compose.activityRule.scenario.recreate()
        }
        compose.waitUntil(15000) {
            compose.onAllNodesWithText("+  Registrar proveedor").fetchSemanticsNodes().singleOrNull()
                ?.config?.contains(SemanticsProperties.Disabled) == false
        }
        compose.onNodeWithContentDescription("Abrir menú").performClick()
        compose.onNode(hasText("Resumen") and hasClickAction()).performClick()
        compose.onNodeWithText("Ver proveedores").performScrollTo().performClick()
        try {
            compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
            compose.waitUntil(15000) { compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE }
            compose.onNode(hasScrollToIndexAction()).performScrollToIndex(5)
            compose.onNodeWithText("Correo", substring = false).performScrollTo().assertIsDisplayed()
            CapturaPantalla.guardar(compose, "crm-tabla")
        } finally {
            compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        }
    }
}
