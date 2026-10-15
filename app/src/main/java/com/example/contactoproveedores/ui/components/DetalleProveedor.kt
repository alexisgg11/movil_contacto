package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R
import com.example.contactoproveedores.domain.model.Proveedor

@Composable
fun DetalleProveedor(p: Proveedor) {
    Column(Modifier.fillMaxWidth().padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        DatoContacto(R.drawable.ic_phone, "Teléfono", p.telefono)
        DatoContacto(R.drawable.ic_mail, "Correo electrónico", p.correo)
        DatoContacto(R.drawable.ic_location, "Dirección", p.direccion)
    }
}
