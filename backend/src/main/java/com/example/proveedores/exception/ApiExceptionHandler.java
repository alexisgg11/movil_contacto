package com.example.proveedores.exception;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ProveedorDuplicadoException.class)
    public ResponseEntity<Map<String, String>> duplicado(ProveedorDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }
    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> ausente(ProveedorNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e) {
        String campos = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField()).distinct().sorted().collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(Map.of("message",
            "Datos inválidos en: " + campos + ". Revisa campos obligatorios, longitudes y formatos."));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> integridad(DataIntegrityViolationException e) {
        Throwable causa = e;
        while (causa.getCause() != null && causa.getCause() != causa) causa = causa.getCause();
        if (causa instanceof java.sql.SQLException sql && "23505".equals(sql.getSQLState()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message",
                "Ya existe un proveedor con esa empresa. Actualiza el directorio antes de volver a intentar."));
        return ResponseEntity.badRequest().body(Map.of("message", "Los datos no se pudieron guardar. Revisa el formulario."));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> cuerpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("message", "La solicitud no contiene un formulario válido."));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message",
            e.getReason() == null ? "No se pudo completar la solicitud" : e.getReason()));
    }
}
