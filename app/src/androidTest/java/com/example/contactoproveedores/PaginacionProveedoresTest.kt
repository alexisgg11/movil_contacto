package com.example.contactoproveedores

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.contactoproveedores.ui.components.PaginacionProveedores
import org.junit.Rule
import org.junit.Test

class PaginacionProveedoresTest {
    @get:Rule val compose = createComposeRule()
    @Test fun navegaYDeshabilitaLosLimites() {
        compose.setContent {
            var pagina by remember { mutableStateOf(0) }
            MaterialTheme { PaginacionProveedores(pagina, 3, 11, 5, false) { pagina = it } }
        }
        compose.onNodeWithContentDescription("Página anterior").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Página siguiente").performClick()
        compose.onNodeWithText("6–10 de 11").assertIsDisplayed()
        compose.onNodeWithContentDescription("Página siguiente").performClick()
        compose.onNodeWithText("11–11 de 11").assertIsDisplayed()
        compose.onNodeWithContentDescription("Página siguiente").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Página anterior").performClick()
        compose.onNodeWithText("Página 2 de 3").assertIsDisplayed()
    }
}
