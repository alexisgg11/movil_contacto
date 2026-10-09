package com.example.contactoproveedores.ui.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.domain.*

@Composable
fun FormularioProveedor(p: Proveedor, errores: Map<Campo, String>, ocupado: Boolean, puedeAgregar: Boolean,
    editar: (Campo, String) -> Unit, agregar: () -> Unit) {
    val teclado = LocalSoftwareKeyboardController.current
    OutlinedCard(shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Nuevo proveedor", style = MaterialTheme.typography.titleLarge)
            Text("Completa los seis campos del contacto.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Campo.entries.forEach { campo ->
                    val mensajeError = errores[campo]
                    OutlinedTextField(value = p.valor(campo), onValueChange = { editar(campo, it) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), label = { Text(campo.etiqueta) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        enabled = !ocupado, singleLine = true, isError = errores.containsKey(campo),
                        supportingText = if (mensajeError == null) null else { { Text(mensajeError) } },
                        shape = RoundedCornerShape(10.dp),
                        keyboardActions = KeyboardActions(onDone = { teclado?.hide() }),
                        keyboardOptions = KeyboardOptions(imeAction = if (campo == Campo.RUBRO) ImeAction.Done else ImeAction.Next,
                            keyboardType = when(campo) {
                            Campo.TELEFONO -> KeyboardType.Phone
                            Campo.CORREO -> KeyboardType.Email
                            else -> KeyboardType.Text
                        }))
                }
            }
            Button(onClick = { teclado?.hide(); agregar() }, enabled = !ocupado && puedeAgregar, shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                Text(if (ocupado) "Procesando…" else "+  Agregar proveedor")
            }
            Text("Todos los campos son obligatorios.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
