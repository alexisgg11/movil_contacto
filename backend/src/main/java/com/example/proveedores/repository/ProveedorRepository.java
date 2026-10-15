package com.example.proveedores.repository;

import com.example.proveedores.entity.Proveedor;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByEmpresaClaveAndIdNot(String empresaClave, Long id);

    @Query("""
        select p from Proveedor p
        where (:activo is null or p.activo = :activo)
        and (locate(lower(:texto), lower(p.empresa)) > 0
          or locate(lower(:texto), lower(p.contacto)) > 0
          or locate(lower(:texto), lower(p.telefono)) > 0
          or locate(lower(:texto), lower(p.correo)) > 0
          or locate(lower(:texto), lower(p.direccion)) > 0
          or locate(lower(:texto), lower(p.rubro)) > 0)
        """)
    List<Proveedor> buscar(@Param("texto") String texto, @Param("activo") Boolean activo, Sort sort);
}
