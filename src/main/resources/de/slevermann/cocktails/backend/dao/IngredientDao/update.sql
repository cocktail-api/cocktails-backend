WITH i AS (UPDATE ingredient
    SET name = :name,
        description = :description,
        type = (SELECT id FROM ingredient_type it WHERE it.uuid = :type)
    WHERE uuid = :uuid
    RETURNING uuid, name, description, type)
SELECT i.uuid        AS ingredient_uuid,
       i.name        AS ingredient_name,
       i.description AS ingredient_description,
       it.name       AS type_name,
       it.uuid       AS type_uuid
FROM i
         JOIN ingredient_type it ON i.type = it.id
