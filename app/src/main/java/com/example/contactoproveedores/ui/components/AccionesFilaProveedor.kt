package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R

@Composable
fun AccionesFilaProveedor(activo: Boolean, ocupado: Boolean, editar: () -> Unit, cambiarEstado: () -> Unit) {
    Row {
        IconButton(onClick = editar, enabled = !ocupado, modifier = Modifier.size(48.dp)) {
            Icon(painterResource(R.drawable.ic_edit), "Editar proveedor", Modifier.size(20.dp))
        }
        IconButton(onClick = cambiarEstado, enabled = !ocupado, modifier = Modifier.size(48.dp)) {
            Icon(painterResource(R.drawable.ic_power), if (activo) "Desactivar proveedor" else "Reactivar proveedor",
                Modifier.size(20.dp), tint = if (activo) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
    }
}
