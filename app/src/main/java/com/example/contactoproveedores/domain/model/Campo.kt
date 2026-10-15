package com.example.contactoproveedores.domain.model

enum class Campo(val etiqueta: String, val limite: Int) {
    EMPRESA("Empresa", 120), CONTACTO("Persona de contacto", 120), TELEFONO("Teléfono", 20),
    CORREO("Correo electrónico", 160), DIRECCION("Dirección", 200), RUBRO("Rubro", 80)
}
