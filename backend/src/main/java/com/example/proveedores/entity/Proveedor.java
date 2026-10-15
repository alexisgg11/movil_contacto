package com.example.proveedores.entity;

import com.example.proveedores.validation.NormalizarEmpresa;
import jakarta.persistence.*;

@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120) private String empresa;
    @Column(name = "empresa_clave", nullable = false, length = 120, unique = true)
    private String empresaClave;
    @Column(nullable = false, length = 120) private String contacto;
    @Column(nullable = false, length = 20) private String telefono;
    @Column(nullable = false, length = 160) private String correo;
    @Column(nullable = false, length = 200) private String direccion;
    @Column(nullable = false, length = 80) private String rubro;
    @Column(nullable = false) private boolean activo = true;

    protected Proveedor() {}
    public Proveedor(String empresa, String contacto, String telefono, String correo,
                     String direccion, String rubro) {
        actualizar(empresa, contacto, telefono, correo, direccion, rubro);
    }
    public void actualizar(String empresa, String contacto, String telefono, String correo,
                           String direccion, String rubro) {
        this.empresa = empresa;
        this.empresaClave = NormalizarEmpresa.clave(empresa);
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.rubro = rubro;
    }
    public void cambiarEstado(boolean activo) { this.activo = activo; }
    public Long getId() { return id; }
    public String getEmpresa() { return empresa; }
    public String getContacto() { return contacto; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getRubro() { return rubro; }
    public boolean isActivo() { return activo; }
}
