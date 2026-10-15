package com.example.proveedores.exception;

public class ProveedorNoEncontradoException extends RuntimeException {
    public ProveedorNoEncontradoException() { super("El proveedor ya no existe. Actualiza el directorio."); }
}
