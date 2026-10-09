package com.example.proveedores.entity;
import jakarta.persistence.*;
import com.example.proveedores.dto.ProveedorRequest;
@Entity
@Table(name = "proveedores")
public class Proveedor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String empresa;
    @Column(nullable = false, length = 120) private String contacto;
    @Column(nullable = false, length = 20) private String telefono;
    @Column(nullable = false, length = 160) private String correo;
    @Column(nullable = false, length = 200) private String direccion;
    @Column(nullable = false, length = 80) private String rubro;
    protected Proveedor() {}
    public Proveedor(ProveedorRequest r) {
        empresa = r.empresa(); contacto = r.contacto(); telefono = r.telefono();
        correo = r.correo(); direccion = r.direccion(); rubro = r.rubro();
    }
    public Long getId() { return id; }
    public String getEmpresa() { return empresa; }
    public String getContacto() { return contacto; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public String getDireccion() { return direccion; }
    public String getRubro() { return rubro; }
}
