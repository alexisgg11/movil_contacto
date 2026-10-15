ALTER TABLE proveedores ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE proveedores ADD COLUMN empresa_clave VARCHAR(120);
UPDATE proveedores SET empresa_clave = lower(regexp_replace(btrim(empresa), '\s+', ' ', 'g'));
ALTER TABLE proveedores ALTER COLUMN empresa_clave SET NOT NULL;
CREATE UNIQUE INDEX uk_proveedores_empresa_clave ON proveedores (empresa_clave);
