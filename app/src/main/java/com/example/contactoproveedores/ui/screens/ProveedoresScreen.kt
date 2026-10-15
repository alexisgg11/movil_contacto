package com.example.contactoproveedores.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.R
import com.example.contactoproveedores.ui.components.*
import com.example.contactoproveedores.ui.formulario.ModalProveedor
import com.example.contactoproveedores.ui.navigation.*
import com.example.contactoproveedores.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProveedoresScreen(state: ProveedoresState, acciones: AccionesProveedores) {
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var seccion by rememberSaveable { mutableStateOf(SeccionPanel.PROVEEDORES) }
    ModalNavigationDrawer(drawerState = drawer, drawerContent = {
        MenuPanel(seccion) { seccion = it; scope.launch { drawer.close() } }
    }) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column {
                    TopAppBar(title = {
                        Column {
                            Text("Contacto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Gestión de proveedores", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }, navigationIcon = {
                        IconButton(onClick = { scope.launch { drawer.open() } }) {
                            Icon(painterResource(R.drawable.ic_menu), "Abrir menú", Modifier.size(22.dp))
                        }
                    }, actions = {
                        IconButton(onClick = acciones.recargar, enabled = !state.ocupado) {
                            Icon(painterResource(R.drawable.ic_refresh), "Actualizar directorio", Modifier.size(22.dp))
                        }
                    }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }) { padding ->
            BoxWithConstraints(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                val tabla = maxWidth >= 840.dp
                LazyColumn(Modifier.widthIn(max = 1200.dp).fillMaxSize(),
                    contentPadding = PaddingValues(if (tabla) 32.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Column(Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(seccion.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text("Organiza tus contactos y mantén sus datos al día.",
                                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    item { ResumenProveedores(state.proveedores.size, state.activos, state.inactivos) }
                    if (seccion == SeccionPanel.RESUMEN) {
                        item {
                            Column(Modifier.fillMaxWidth().padding(vertical = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Tu directorio, bajo control", style = MaterialTheme.typography.titleLarge)
                                Text("Registra empresas, edita sus datos y cambia su estado sin perder información.")
                                Button(onClick = { seccion = SeccionPanel.PROVEEDORES }) { Text("Ver proveedores") }
                            }
                        }
                    } else {
                        item {
                            Button(onClick = acciones.nuevo, enabled = state.cargado && !state.ocupado,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) {
                                Text("+  Registrar proveedor")
                            }
                        }
                        item { CabeceraDirectorio(state.busqueda, state.filtro, state.ocupado,
                            acciones.buscar, acciones.buscarConfirmado, acciones.filtrar) }
                        item {
                            Text("Directorio", style = MaterialTheme.typography.titleSmall)
                            if (state.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp))
                            state.aviso?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall) }
                        }
                        if (state.error != null || (!state.ocupado && state.resultados.isEmpty())) item {
                            EstadoDirectorio(state.cargado, state.busqueda.isNotBlank() || state.proveedores.isNotEmpty(),
                                state.error, acciones.recargar)
                        }
                        if (tabla && state.resultados.isNotEmpty()) item { CabeceraTablaProveedores() }
                        items(state.proveedoresPagina, key = { it.id }) { p ->
                            TarjetaProveedor(p, state.ocupado, { acciones.abrirEdicion(p) }, { acciones.confirmarEstado(p) }, tabla)
                        }
                        if (state.resultados.isNotEmpty()) item {
                            PaginacionProveedores(state.paginaActual, state.totalPaginas, state.resultados.size,
                                state.tamanioPagina, state.ocupado, acciones.cambiarPagina)
                        }
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
    if (state.formularioAbierto) ModalProveedor(state, acciones.editarCampo, acciones.guardar, acciones.cerrarFormulario)
    state.cambioEstado?.let { ConfirmarEstadoProveedor(it, state.ocupado, acciones.cambiarEstado) { acciones.confirmarEstado(null) } }
    state.modal?.let { ModalResultado(it, acciones.cerrarModal) }
}
