-- La unicidad sigue protegida por uk_proveedores_empresa_normalizada (V2).
-- La expresión del índice calcula la normalización directamente desde empresa.
DROP INDEX IF EXISTS uk_proveedores_empresa_clave;
ALTER TABLE proveedores DROP COLUMN empresa_clave;
