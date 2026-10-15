package com.example.proveedores.service;

import com.example.proveedores.dto.ProveedorRequest;
import com.example.proveedores.dto.ProveedorResponse;
import com.example.proveedores.entity.Proveedor;
import com.example.proveedores.exception.ProveedorDuplicadoException;
import com.example.proveedores.exception.ProveedorNoEncontradoException;
import com.example.proveedores.mapper.ProveedorMapper;
import com.example.proveedores.repository.ProveedorRepository;
import com.example.proveedores.validation.NormalizarEmpresa;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProveedorService {
    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;

    public ProveedorService(ProveedorRepository repository, ProveedorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public List<ProveedorResponse> listar(String busqueda, Boolean activo) {
        return repository.buscar(busqueda.trim(), activo, Sort.by(Sort.Direction.DESC, "id"))
            .stream().map(mapper::respuesta).toList();
    }
    public ProveedorResponse obtener(Long id) { return mapper.respuesta(encontrar(id)); }

    @Transactional
    public ProveedorResponse agregar(ProveedorRequest request) {
        comprobarDuplicado(request.empresa(), 0L);
        return mapper.respuesta(repository.saveAndFlush(mapper.crear(request)));
    }
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = encontrar(id);
        comprobarDuplicado(request.empresa(), id);
        mapper.actualizar(proveedor, request);
        return mapper.respuesta(repository.saveAndFlush(proveedor));
    }
    @Transactional
    public ProveedorResponse cambiarEstado(Long id, boolean activo) {
        Proveedor proveedor = encontrar(id);
        proveedor.cambiarEstado(activo);
        return mapper.respuesta(repository.saveAndFlush(proveedor));
    }
    private Proveedor encontrar(Long id) {
        return repository.findById(id).orElseThrow(ProveedorNoEncontradoException::new);
    }
    private void comprobarDuplicado(String empresa, Long id) {
        if (repository.existsByEmpresaClaveAndIdNot(NormalizarEmpresa.clave(empresa), id))
            throw new ProveedorDuplicadoException();
    }
}
