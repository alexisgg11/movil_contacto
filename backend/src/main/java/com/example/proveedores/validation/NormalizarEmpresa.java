package com.example.proveedores.validation;

import java.util.Locale;

public final class NormalizarEmpresa {
    private NormalizarEmpresa() {}
    public static String clave(String empresa) {
        return empresa.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
