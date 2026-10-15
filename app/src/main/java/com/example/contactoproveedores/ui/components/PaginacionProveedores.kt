package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R

@Composable
fun PaginacionProveedores(pagina: Int, paginas: Int, total: Int, tamanio: Int, ocupado: Boolean,
                         cambiar: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${pagina * tamanio + 1}–${minOf((pagina + 1) * tamanio, total)} de $total",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { cambiar(pagina - 1) }, enabled = pagina > 0 && !ocupado) {
                    Icon(painterResource(R.drawable.ic_previous), "Página anterior", Modifier.size(20.dp))
                }
                Text("Página ${pagina + 1} de $paginas", style = MaterialTheme.typography.labelMedium)
                IconButton(onClick = { cambiar(pagina + 1) }, enabled = pagina < paginas - 1 && !ocupado) {
                    Icon(painterResource(R.drawable.ic_next), "Página siguiente", Modifier.size(20.dp))
                }
            }
        }
    }
}
