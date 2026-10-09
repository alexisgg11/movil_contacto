package com.example.contactoproveedores.data
import com.example.contactoproveedores.domain.Proveedor
import org.json.JSONObject
internal object ProveedorJson {
    fun escribir(p: Proveedor) = JSONObject().apply {
        put("empresa", p.empresa); put("contacto", p.contacto); put("telefono", p.telefono)
        put("correo", p.correo); put("direccion", p.direccion); put("rubro", p.rubro)
    }.toString()
    fun leer(j: JSONObject) = Proveedor(j.getLong("id"), j.getString("empresa"),
        j.getString("contacto"), j.getString("telefono"), j.getString("correo"),
        j.getString("direccion"), j.getString("rubro"))
}
