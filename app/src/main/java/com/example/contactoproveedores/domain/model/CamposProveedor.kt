package com.example.contactoproveedores.domain.model

fun Proveedor.valor(campo: Campo): String = when (campo) {
    Campo.EMPRESA -> empresa; Campo.CONTACTO -> contacto; Campo.TELEFONO -> telefono
    Campo.CORREO -> correo; Campo.DIRECCION -> direccion; Campo.RUBRO -> rubro
}
fun Proveedor.actualizar(campo: Campo, texto: String): Proveedor = when (campo) {
    Campo.EMPRESA -> copy(empresa = texto); Campo.CONTACTO -> copy(contacto = texto)
    Campo.TELEFONO -> copy(telefono = texto); Campo.CORREO -> copy(correo = texto)
    Campo.DIRECCION -> copy(direccion = texto); Campo.RUBRO -> copy(rubro = texto)
}
