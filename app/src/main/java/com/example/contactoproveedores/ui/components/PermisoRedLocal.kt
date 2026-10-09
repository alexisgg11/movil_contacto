package com.example.contactoproveedores.ui.components

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/** Android 17 exige permiso para conectar con el servidor en la red local. */
@Composable
fun PermisoRedLocal(content: @Composable () -> Unit) {
    val contexto = LocalContext.current
    val permiso = "android.permission.ACCESS_LOCAL_NETWORK"
    var permitido by remember {
        mutableStateOf(Build.VERSION.SDK_INT < 37 ||
            ContextCompat.checkSelfPermission(contexto, permiso) == PackageManager.PERMISSION_GRANTED)
    }
    val solicitud = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permitido = it
    }
    if (permitido) content()
    else Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.systemBarsPadding().padding(28.dp),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
            Text("Conecta tu directorio", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            Text("Permite el acceso a la red local para conectar con el directorio de tu negocio.")
            Spacer(Modifier.height(24.dp))
            Button(onClick = { solicitud.launch(permiso) }) { Text("Permitir conexión") }
        }
    }
}
