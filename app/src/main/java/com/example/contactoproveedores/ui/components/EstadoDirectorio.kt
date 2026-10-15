package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EstadoDirectorio(cargado: Boolean, buscando: Boolean, error: String?, recargar: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(when { error != null -> "No se pudo cargar el panel"
            !cargado -> "Conecta tu directorio"
            buscando -> "Sin coincidencias"
            else -> "Aún no hay proveedores" }, style = MaterialTheme.typography.titleMedium)
        Text(error ?: if (buscando) "Prueba otro texto o selecciona otro estado."
            else "Registra tu primer proveedor para comenzar.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (error != null || !cargado) OutlinedButton(onClick = recargar) { Text("Reintentar conexión") }
    }
}
