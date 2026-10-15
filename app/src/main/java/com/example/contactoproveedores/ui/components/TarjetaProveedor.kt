package com.example.contactoproveedores.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R
import com.example.contactoproveedores.domain.model.Proveedor

@Composable
fun TarjetaProveedor(p: Proveedor, ocupado: Boolean, editar: () -> Unit,
                     cambiarEstado: () -> Unit, tabla: Boolean = false) {
    var expandido by rememberSaveable(p.id) { mutableStateOf(false) }
    OutlinedCard(onClick = { expandido = !expandido },
        modifier = Modifier.fillMaxWidth().semantics {
            stateDescription = if (expandido) "Detalle expandido" else "Detalle contraído"
        }, shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!tabla) InicialesEmpresa(p.empresa)
                Column(Modifier.weight(if (tabla) 2f else 1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(p.empresa, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(p.contacto, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("P-" + p.id.toString().padStart(4, '0') + " · " + p.rubro,
                        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (tabla) {
                    Text(p.telefono, Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
                    Text(p.correo, Modifier.weight(1.7f), style = MaterialTheme.typography.bodySmall)
                }
                EtiquetaEstado(p.activo)
                if (tabla) Row {
                    AccionesFilaProveedor(p.activo, ocupado, editar, cambiarEstado)
                    IconButton(onClick = { expandido = !expandido }, modifier = Modifier.size(48.dp)) {
                        Icon(painterResource(R.drawable.ic_expand), if (expandido) "Ocultar detalle" else "Ver detalle", Modifier.size(18.dp))
                    }
                }
            }
            if (!tabla) Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { expandido = !expandido }) {
                    Text(if (expandido) "Ocultar detalle" else "Ver detalle")
                    Spacer(Modifier.width(4.dp))
                    Icon(painterResource(R.drawable.ic_expand), null, Modifier.size(16.dp))
                }
                AccionesFilaProveedor(p.activo, ocupado, editar, cambiarEstado)
            }
            AnimatedVisibility(expandido) { DetalleProveedor(p) }
        }
    }
}
