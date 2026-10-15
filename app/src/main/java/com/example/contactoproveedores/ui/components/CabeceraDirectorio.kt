package com.example.contactoproveedores.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R
import com.example.contactoproveedores.domain.model.FiltroEstado

@Composable
fun CabeceraDirectorio(busqueda: String, filtro: FiltroEstado, ocupado: Boolean,
    buscar: (String) -> Unit, confirmar: () -> Unit, filtrar: (FiltroEstado) -> Unit) {
    val teclado = LocalSoftwareKeyboardController.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = busqueda, onValueChange = buscar, modifier = Modifier.weight(1f),
                singleLine = true, shape = RoundedCornerShape(10.dp), label = { Text("Buscar proveedores") },
                placeholder = { Text("Empresa, contacto o teléfono") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { teclado?.hide(); confirmar() }),
                trailingIcon = {
                    if (busqueda.isNotBlank()) IconButton(onClick = { buscar("") }) {
                        Icon(painterResource(R.drawable.ic_close), "Limpiar búsqueda", Modifier.size(18.dp))
                    }
                })
            FilledIconButton(onClick = { teclado?.hide(); confirmar() }, enabled = !ocupado,
                modifier = Modifier.size(56.dp), shape = RoundedCornerShape(10.dp)) {
                Icon(painterResource(R.drawable.ic_search), "Buscar proveedores", Modifier.size(22.dp))
            }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FiltroEstado.entries.forEach { opcion ->
                FilterChip(selected = filtro == opcion, onClick = { filtrar(opcion) },
                    label = { Text(opcion.etiqueta) }, enabled = !ocupado)
            }
        }
    }
}
