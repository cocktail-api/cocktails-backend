DELETE
FROM cocktail_ingredient ci
    USING cocktail as c, ingredient as i
WHERE c.id = ci.cocktail
  AND i.id = ci.ingredient
  AND c.uuid = :cocktail
  AND i.uuid = :ingredient;
