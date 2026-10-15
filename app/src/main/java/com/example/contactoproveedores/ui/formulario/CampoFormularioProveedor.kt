package com.example.contactoproveedores.ui.formulario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.contactoproveedores.domain.model.Campo

@Composable
fun CampoFormularioProveedor(campo: Campo, valor: String, error: String?, ocupado: Boolean,
                            cambiar: (String) -> Unit) {
    OutlinedTextField(value = valor, onValueChange = cambiar, label = { Text(campo.etiqueta) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), singleLine = true,
        enabled = !ocupado, isError = error != null, shape = RoundedCornerShape(10.dp),
        supportingText = if (error == null) null else { { Text(error) } },
        keyboardOptions = KeyboardOptions(keyboardType = when (campo) {
            Campo.TELEFONO -> KeyboardType.Phone
            Campo.CORREO -> KeyboardType.Email
            else -> KeyboardType.Text
        }))
}
