package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.domain.model.RubroProveedor
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltroRubroProveedor(valor: String?, rubrosRegistrados: List<String>, ocupado: Boolean,
    cambiar: (String?) -> Unit) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    val opciones = (RubroProveedor.entries.map { it.etiqueta } + rubrosRegistrados)
        .map { it.trim() }.filter { it.isNotBlank() }.distinctBy { it.lowercase(Locale.ROOT) }
    val actual = opciones.firstOrNull { it.equals(valor, ignoreCase = true) } ?: valor
    ExposedDropdownMenuBox(expanded = expandido && !ocupado,
        onExpandedChange = { if (!ocupado) expandido = it }) {
        OutlinedTextField(value = actual ?: "Todos los rubros", onValueChange = {},
            readOnly = true, enabled = !ocupado, singleLine = true,
            label = { Text("Filtrar por rubro") },
            modifier = Modifier.fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, !ocupado),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandido) },
            shape = RoundedCornerShape(10.dp))
        ExposedDropdownMenu(expanded = expandido && !ocupado, onDismissRequest = { expandido = false },
            modifier = Modifier.heightIn(max = 320.dp)) {
            DropdownMenuItem(text = { Text("Todos los rubros") },
                onClick = { cambiar(null); expandido = false })
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) },
                    onClick = { cambiar(opcion); expandido = false })
            }
        }
    }
}
