package com.example.contactoproveedores.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuPanel(seccion: SeccionPanel, seleccionar: (SeccionPanel) -> Unit) {
    ModalDrawerSheet {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Contacto", style = MaterialTheme.typography.headlineSmall)
            Text("Gestión de proveedores", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))
        SeccionPanel.entries.forEach { opcion ->
            NavigationDrawerItem(label = { Text(opcion.titulo) }, selected = seccion == opcion,
                onClick = { seleccionar(opcion) }, modifier = Modifier.padding(horizontal = 12.dp))
        }
    }
}
