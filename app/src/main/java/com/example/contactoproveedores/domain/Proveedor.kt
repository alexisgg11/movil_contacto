package com.example.contactoproveedores.domain

data class Proveedor(
    val id: Long = 0, val empresa: String = "", val contacto: String = "",
    val telefono: String = "", val correo: String = "", val direccion: String = "", val rubro: String = ""
) {
    fun normalizado() = copy(empresa = empresa.trim(), contacto = contacto.trim(),
        telefono = telefono.trim(), correo = correo.trim(), direccion = direccion.trim(), rubro = rubro.trim())
}
enum class Campo(val etiqueta: String, val limite: Int) {
    EMPRESA("Empresa", 120), CONTACTO("Persona de contacto", 120), TELEFONO("Teléfono", 20),
    CORREO("Correo electrónico", 160), DIRECCION("Dirección", 200), RUBRO("Rubro", 80)
}
fun Proveedor.valor(campo: Campo): String = when (campo) {
    Campo.EMPRESA -> empresa; Campo.CONTACTO -> contacto; Campo.TELEFONO -> telefono
    Campo.CORREO -> correo; Campo.DIRECCION -> direccion; Campo.RUBRO -> rubro
}
fun Proveedor.actualizar(campo: Campo, texto: String): Proveedor = when (campo) {
    Campo.EMPRESA -> copy(empresa = texto); Campo.CONTACTO -> copy(contacto = texto)
    Campo.TELEFONO -> copy(telefono = texto); Campo.CORREO -> copy(correo = texto)
    Campo.DIRECCION -> copy(direccion = texto); Campo.RUBRO -> copy(rubro = texto)
}
