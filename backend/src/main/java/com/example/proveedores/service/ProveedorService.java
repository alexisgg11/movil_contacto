package com.example.proveedores.service;
import java.util.List;
import com.example.proveedores.dto.*;
import com.example.proveedores.entity.Proveedor;
import com.example.proveedores.repository.ProveedorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@Service
@Transactional(readOnly = true)
public class ProveedorService {
    private final ProveedorRepository repository;
    public ProveedorService(ProveedorRepository repository) { this.repository = repository; }
    public List<ProveedorResponse> listar(String busqueda) {
        String termino = busqueda.trim().toLowerCase(java.util.Locale.ROOT);
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
            .filter(p -> List.of(p.getEmpresa(), p.getContacto(), p.getTelefono(), p.getCorreo(),
                p.getDireccion(), p.getRubro()).stream()
                .anyMatch(v -> v.toLowerCase(java.util.Locale.ROOT).contains(termino)))
            .map(ProveedorResponse::de).toList();
    }
    @Transactional
    public ProveedorResponse agregar(ProveedorRequest request) {
        return ProveedorResponse.de(repository.save(new Proveedor(request)));
    }
    @Transactional
    public void eliminar(Long id) {
        Proveedor p = repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "El proveedor ya no existe"));
        repository.delete(p);
    }
}
