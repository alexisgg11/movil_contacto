package com.example.proveedores.controller;
import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import com.example.proveedores.dto.*;
import com.example.proveedores.service.ProveedorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorService service;
    public ProveedorController(ProveedorService service) { this.service = service; }
    @GetMapping
    public List<ProveedorResponse> listar(@RequestParam(name = "busqueda", defaultValue = "") String busqueda) {
        return service.listar(busqueda);
    }
    @PostMapping
    public ResponseEntity<ProveedorResponse> agregar(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse p = service.agregar(request);
        return ResponseEntity.created(URI.create("/api/proveedores/" + p.id())).body(p);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
