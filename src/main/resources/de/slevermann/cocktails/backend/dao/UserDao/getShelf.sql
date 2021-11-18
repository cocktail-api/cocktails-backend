SELECT i.uuid        AS ingredient_uuid,
       i.name        AS ingredient_name,
       i.description AS ingredient_description,
       it.uuid       AS type_uuid,
       it.name       AS type_name
FROM ingredient AS i
         JOIN ingredient_type it ON it.id = i.type
         JOIN user_ingredient ui ON i.id = ui.ingredient
         JOIN "user" u ON u.id = ui."user"
WHERE u.uuid = :uuid;
