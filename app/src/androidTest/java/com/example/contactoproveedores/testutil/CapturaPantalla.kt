package com.example.contactoproveedores.testutil

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File

object CapturaPantalla {
    fun guardar(compose: ComposeTestRule, nombre: String, tituloModal: String? = null) {
        compose.waitForIdle()
        val contenido = if (tituloModal != null) hasText(tituloModal) else hasContentDescription("Abrir menú")
        val imagen = compose.onNode(isRoot() and hasAnyDescendant(contenido)).captureToImage().asAndroidBitmap()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val archivo = File(instrumentation.targetContext.getExternalFilesDir(null), "$nombre.png")
        archivo.outputStream().use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val salida = instrumentation.uiAutomation.executeShellCommand("cp ${archivo.absolutePath} /sdcard/$nombre.png")
        ParcelFileDescriptor.AutoCloseInputStream(salida).use { it.readBytes() }
    }
}
