package com.example.proveedores.exception;

public class ProveedorDuplicadoException extends RuntimeException {
    public ProveedorDuplicadoException() {
        super("Ya existe una empresa con ese nombre, incluso si está inactiva. Busca su registro para editarlo o reactivarlo.");
    }
}
