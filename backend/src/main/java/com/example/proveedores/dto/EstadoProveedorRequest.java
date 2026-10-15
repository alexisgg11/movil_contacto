package com.example.proveedores.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoProveedorRequest(@NotNull Boolean activo) {}
