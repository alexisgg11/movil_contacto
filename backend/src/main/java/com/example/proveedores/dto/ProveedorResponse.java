package com.example.proveedores.dto;
import com.example.proveedores.entity.Proveedor;
public record ProveedorResponse(Long id, String empresa, String contacto, String telefono,
    String correo, String direccion, String rubro) {
    public static ProveedorResponse de(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getEmpresa(), p.getContacto(), p.getTelefono(),
            p.getCorreo(), p.getDireccion(), p.getRubro());
    }
}
