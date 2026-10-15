package com.example.contactoproveedores.ui.formulario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.contactoproveedores.domain.model.*
import com.example.contactoproveedores.viewmodel.ProveedoresState

@Composable
fun ModalProveedor(state: ProveedoresState, editar: (Campo, String) -> Unit, guardar: () -> Unit, cerrar: () -> Unit) {
    val teclado = LocalSoftwareKeyboardController.current
    Dialog(onDismissRequest = cerrar, properties = DialogProperties(
        usePlatformDefaultWidth = false, dismissOnBackPress = !state.ocupado, dismissOnClickOutside = false)) {
        Surface(Modifier.widthIn(max = 560.dp).fillMaxWidth(.94f).fillMaxHeight(.92f).imePadding(),
            shape = MaterialTheme.shapes.large) {
            Column {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (state.formulario.id == 0L) "Nuevo proveedor" else "Editar proveedor",
                        style = MaterialTheme.typography.headlineSmall)
                    Text("Completa los datos de contacto. Todos los campos son obligatorios.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                HorizontalDivider()
                Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Campo.entries.forEach { campo ->
                        CampoFormularioProveedor(campo, state.formulario.valor(campo), state.errores[campo],
                            state.ocupado) { editar(campo, it) }
                    }
                }
                if (state.ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = cerrar, enabled = !state.ocupado, shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text("Cancelar")
                    }
                    Button(onClick = { teclado?.hide(); guardar() }, enabled = !state.ocupado && state.cargado,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp)) {
                        Text(if (state.ocupado) "Guardando…" else "Guardar")
                    }
                }
            }
        }
    }
}
