package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R

@Composable
fun CabeceraDirectorio(total: Int, resultados: Int, busqueda: String, ocupado: Boolean,
    buscar: (String) -> Unit, recargar: () -> Unit, buscarConfirmado: () -> Unit) {
    val teclado = LocalSoftwareKeyboardController.current
    val consultar = { teclado?.hide(); buscarConfirmado() }
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Directorio de proveedores", style = MaterialTheme.typography.titleMedium)
                val contador = if (busqueda.isBlank())
                    if (total == 1) "1 proveedor" else "$total proveedores"
                else "$resultados de $total proveedores"
                Text(contador, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = recargar, enabled = !ocupado, modifier = Modifier.size(48.dp)) {
                Icon(painterResource(R.drawable.ic_refresh), contentDescription = "Actualizar directorio",
                    modifier = Modifier.size(22.dp))
            }
        }
        OutlinedTextField(value = busqueda, onValueChange = buscar, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { consultar() }),
            singleLine = true, label = { Text("Buscar proveedores") },
            placeholder = { Text("Empresa o contacto") }, shape = RoundedCornerShape(10.dp),
            leadingIcon = { Icon(painterResource(R.drawable.ic_search), contentDescription = null,
                modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (busqueda.isNotBlank()) IconButton(onClick = { buscar("") }) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = "Limpiar búsqueda",
                        modifier = Modifier.size(20.dp))
                }
            })
        OutlinedButton(onClick = { consultar() }, enabled = !ocupado,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Buscar") }
    }
}
