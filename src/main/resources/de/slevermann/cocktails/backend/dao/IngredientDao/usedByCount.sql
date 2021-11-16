SELECT COUNT(*)
FROM cocktail_ingredient
         JOIN ingredient i on i.id = cocktail_ingredient.ingredient
WHERE i.uuid = :uuid
