package com.example.contactoproveedores.ui.formulario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.domain.model.RubroProveedor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorRubroProveedor(valor: String, error: String?, ocupado: Boolean, cambiar: (String) -> Unit) {
    var expandido by rememberSaveable { mutableStateOf(false) }
    val catalogo = RubroProveedor.entries.map { it.etiqueta }
    val actual = catalogo.firstOrNull { it.equals(valor, ignoreCase = true) } ?: valor
    val opciones = if (actual.isNotBlank() && actual !in catalogo) catalogo + actual else catalogo
    ExposedDropdownMenuBox(expanded = expandido && !ocupado, onExpandedChange = { if (!ocupado) expandido = it }) {
        OutlinedTextField(value = actual, onValueChange = {}, readOnly = true, enabled = !ocupado,
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, !ocupado),
            label = { Text("Rubro") }, placeholder = { Text("Selecciona un rubro") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandido) },
            isError = error != null, supportingText = if (error == null) null else { { Text(error) } },
            shape = RoundedCornerShape(10.dp))
        ExposedDropdownMenu(expanded = expandido && !ocupado, onDismissRequest = { expandido = false },
            modifier = Modifier.heightIn(max = 320.dp)) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = { cambiar(opcion); expandido = false })
            }
        }
    }
}
