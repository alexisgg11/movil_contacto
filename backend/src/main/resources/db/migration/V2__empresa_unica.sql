-- Conserva los registros; impide duplicados incluso con solicitudes simultáneas.
CREATE UNIQUE INDEX uk_proveedores_empresa_normalizada
ON proveedores (lower(regexp_replace(btrim(empresa), '\s+', ' ', 'g')));
