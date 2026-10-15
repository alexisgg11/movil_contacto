package com.example.proveedores.dto;

public record ProveedorResponse(Long id, String empresa, String contacto, String telefono,
    String correo, String direccion, String rubro, boolean activo) {}
