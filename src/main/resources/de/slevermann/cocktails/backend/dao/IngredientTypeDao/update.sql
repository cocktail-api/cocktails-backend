UPDATE ingredient_type
SET name = :name
WHERE uuid = :uuid
RETURNING uuid AS type_uuid, name AS type_name;
