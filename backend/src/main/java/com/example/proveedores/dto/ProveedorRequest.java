package com.example.proveedores.dto;
import jakarta.validation.constraints.*;
public record ProveedorRequest(
    @NotBlank @Size(max = 120) String empresa,
    @NotBlank @Size(max = 120) String contacto,
    @NotBlank @Pattern(regexp = "^\\+?[0-9 ()-]{7,20}$") String telefono,
    @NotBlank @Email @Size(max = 160) String correo,
    @NotBlank @Size(max = 200) String direccion,
    @NotBlank @Size(max = 80) String rubro
) {
    public ProveedorRequest {
        empresa = limpiar(empresa); contacto = limpiar(contacto); telefono = limpiar(telefono);
        correo = limpiar(correo); direccion = limpiar(direccion); rubro = limpiar(rubro);
    }
    private static String limpiar(String valor) { return valor == null ? null : valor.trim(); }
}
