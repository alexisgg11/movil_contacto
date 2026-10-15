package com.example.proveedores.controller;

import com.example.proveedores.dto.*;
import com.example.proveedores.service.ProveedorService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
    private final ProveedorService service;
    public ProveedorController(ProveedorService service) { this.service = service; }

    @GetMapping
    public List<ProveedorResponse> listar(
        @RequestParam(name = "busqueda", defaultValue = "") String busqueda,
        @RequestParam(name = "activo", required = false) Boolean activo) {
        return service.listar(busqueda, activo);
    }
    @GetMapping("/{id}")
    public ProveedorResponse obtener(@PathVariable("id") Long id) { return service.obtener(id); }

    @PostMapping
    public ResponseEntity<ProveedorResponse> agregar(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse p = service.agregar(request);
        return ResponseEntity.created(URI.create("/api/proveedores/" + p.id())).body(p);
    }
    @PutMapping("/{id}")
    public ProveedorResponse actualizar(@PathVariable("id") Long id, @Valid @RequestBody ProveedorRequest request) {
        return service.actualizar(id, request);
    }
    @PutMapping("/{id}/estado")
    public ProveedorResponse cambiarEstado(@PathVariable("id") Long id, @Valid @RequestBody EstadoProveedorRequest request) {
        return service.cambiarEstado(id, request.activo());
    }
    /** Compatibilidad con clientes anteriores: nunca borra físicamente. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable("id") Long id) {
        service.cambiarEstado(id, false);
        return ResponseEntity.noContent().build();
    }
}
