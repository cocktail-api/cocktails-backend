SELECT COUNT(*)
FROM ingredient
         JOIN ingredient_type it on it.id = ingredient.type
WHERE it.uuid = :uuid
