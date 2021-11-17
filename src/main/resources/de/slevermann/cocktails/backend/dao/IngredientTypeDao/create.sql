INSERT INTO ingredient_type (name)
VALUES (:name)
RETURNING uuid AS type_uuid, name AS type_name
