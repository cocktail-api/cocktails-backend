SELECT COUNT(*)
FROM user_ingredient
         JOIN ingredient i on i.id = user_ingredient.ingredient
WHERE i.uuid = :uuid
