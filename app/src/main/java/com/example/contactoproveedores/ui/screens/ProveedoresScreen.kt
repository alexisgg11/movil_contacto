package com.example.contactoproveedores.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.contactoproveedores.domain.*
import com.example.contactoproveedores.ui.components.*
import com.example.contactoproveedores.ui.theme.ContactoProveedoresTheme
import com.example.contactoproveedores.viewmodel.ProveedoresState

@Composable
fun ProveedoresScreen(state: ProveedoresState, editar: (Campo, String) -> Unit,
    buscar: (String) -> Unit, agregar: () -> Unit, confirmar: (Proveedor?) -> Unit,
    eliminar: () -> Unit, recargar: () -> Unit) {
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize().systemBarsPadding().imePadding(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(Modifier.widthIn(max = 600.dp).fillMaxWidth().fillMaxHeight(), contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Column(Modifier.padding(bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("CONTACTO / DIRECTORIO", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary)
                        Text("Proveedores", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("Registra y encuentra tus contactos de negocio.", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                item { FormularioProveedor(state.formulario, state.errores, state.ocupado, state.cargado, editar, agregar) }
                item {
                    Column(Modifier.padding(top = 24.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CabeceraDirectorio(state.proveedores.size, state.resultados.size,
                            state.busqueda, state.ocupado, buscar, recargar)
                        if (state.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
                        state.aviso?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                        state.error?.let {
                            Surface(color = MaterialTheme.colorScheme.errorContainer,
                                shape = MaterialTheme.shapes.medium) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(it, color = MaterialTheme.colorScheme.onErrorContainer)
                                    TextButton(onClick = recargar, enabled = !state.ocupado) { Text("Reintentar conexión") }
                                }
                            }
                        }
                    }
                }
                if (state.resultados.isEmpty() && !state.ocupado) item {
                    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (state.busqueda.isNotBlank()) "Sin coincidencias" else if (state.cargado)
                            "Tu directorio comienza aquí" else "Conecta tu directorio",
                            style = MaterialTheme.typography.titleMedium)
                        Text(if (state.busqueda.isNotBlank()) "Prueba con otro nombre o rubro."
                            else if (state.cargado) "Añade tu primer proveedor con el formulario."
                            else "Inicia el servidor para consultar los proveedores guardados.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(state.resultados, key = { it.id }) { p ->
                    TarjetaProveedor(p, state.ocupado) { confirmar(p) }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
    state.eliminar?.let { p ->
        AlertDialog(onDismissRequest = { if (!state.ocupado) confirmar(null) },
            title = { Text("¿Eliminar proveedor?") },
            text = { Text("Se eliminará ${p.empresa} de la base de datos. Esta acción no se puede deshacer.") },
            confirmButton = { TextButton(onClick = eliminar, enabled = !state.ocupado) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { confirmar(null) }, enabled = !state.ocupado) { Text("Cancelar") } })
    }
}
@Preview(showBackground = true, widthDp = 420, heightDp = 1000)
@Composable
private fun ProveedoresPreview() {
    ContactoProveedoresTheme(darkTheme = false) {
        ProveedoresScreen(ProveedoresState(cargado = true, proveedores = listOf(
            Proveedor(1, "Distribuidora Andina", "Ana Torres", "987654321",
                "ana@andina.pe", "Av. Primavera 120 · Lima", "Suministros"))),
            { _, _ -> }, {}, {}, {}, {}, {})
    }
}
