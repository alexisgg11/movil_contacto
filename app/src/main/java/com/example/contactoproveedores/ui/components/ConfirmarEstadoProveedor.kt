package com.example.contactoproveedores.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.contactoproveedores.domain.model.Proveedor

@Composable
fun ConfirmarEstadoProveedor(p: Proveedor, ocupado: Boolean, confirmar: () -> Unit, cancelar: () -> Unit) {
    AlertDialog(onDismissRequest = { if (!ocupado) cancelar() },
        title = { Text(if (p.activo) "¿Desactivar proveedor?" else "¿Reactivar proveedor?") },
        text = { Text(if (p.activo) "${p.empresa} quedará inactivo. Sus datos se conservarán y podrás reactivarlo."
            else "${p.empresa} volverá a estar activo en el directorio.") },
        confirmButton = { TextButton(onClick = confirmar, enabled = !ocupado) {
            Text(if (p.activo) "Desactivar" else "Reactivar")
        } },
        dismissButton = { TextButton(onClick = cancelar, enabled = !ocupado) { Text("Cancelar") } })
}
