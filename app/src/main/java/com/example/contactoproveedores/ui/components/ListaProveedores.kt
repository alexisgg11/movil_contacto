package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R
import com.example.contactoproveedores.domain.Proveedor

@Composable
fun TarjetaProveedor(p: Proveedor, ocupado: Boolean, eliminar: () -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InicialesEmpresa(p.empresa)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(p.empresa, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(p.contacto, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("P-" + p.id.toString().padStart(4, '0'), style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = eliminar, enabled = !ocupado, modifier = Modifier.size(48.dp)) {
                    Icon(painterResource(R.drawable.ic_delete), contentDescription = "Eliminar proveedor",
                        modifier = Modifier.size(20.dp), tint = if (ocupado)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .38f) else MaterialTheme.colorScheme.error)
                }
            }
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(30.dp)) {
                Text(p.rubro, Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            DatoContacto(R.drawable.ic_phone, "Teléfono", p.telefono)
            DatoContacto(R.drawable.ic_mail, "Correo electrónico", p.correo)
            DatoContacto(R.drawable.ic_location, "Dirección", p.direccion)
        }
    }
}

@Composable
private fun InicialesEmpresa(empresa: String) {
    val iniciales = empresa.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        .take(2).map { it.first() }.joinToString("").uppercase()
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            Text(iniciales, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
