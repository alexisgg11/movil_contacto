package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CabeceraTablaProveedores() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Empresa / contacto", Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
        Text("Teléfono", Modifier.weight(1.2f), style = MaterialTheme.typography.labelMedium)
        Text("Correo", Modifier.weight(1.7f), style = MaterialTheme.typography.labelMedium)
        Text("Estado", Modifier.width(64.dp), style = MaterialTheme.typography.labelMedium)
        Text("Acciones", Modifier.width(144.dp), style = MaterialTheme.typography.labelMedium)
    }
}
