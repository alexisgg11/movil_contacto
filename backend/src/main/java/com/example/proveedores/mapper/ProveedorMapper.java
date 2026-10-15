package com.example.proveedores.mapper;

import com.example.proveedores.dto.ProveedorRequest;
import com.example.proveedores.dto.ProveedorResponse;
import com.example.proveedores.entity.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {
    public Proveedor crear(ProveedorRequest r) {
        return new Proveedor(r.empresa(), r.contacto(), r.telefono(), r.correo(), r.direccion(), r.rubro());
    }
    public void actualizar(Proveedor p, ProveedorRequest r) {
        p.actualizar(r.empresa(), r.contacto(), r.telefono(), r.correo(), r.direccion(), r.rubro());
    }
    public ProveedorResponse respuesta(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getEmpresa(), p.getContacto(), p.getTelefono(),
            p.getCorreo(), p.getDireccion(), p.getRubro(), p.isActivo());
    }
}
