package com.example.contactoproveedores.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.contactoproveedores.viewmodel.MensajeModal

@Composable
fun ModalResultado(mensaje: MensajeModal, cerrar: () -> Unit) {
    AlertDialog(onDismissRequest = cerrar,
        title = { Text(mensaje.titulo) }, text = { Text(mensaje.detalle) },
        confirmButton = { TextButton(onClick = cerrar) { Text("Entendido") } })
}
