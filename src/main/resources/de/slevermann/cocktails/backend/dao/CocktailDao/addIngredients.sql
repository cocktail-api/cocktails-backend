INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
SELECT c.id, i.id, :optional, :garnish, :amount, :unit
FROM cocktail c,
     ingredient i
WHERE c.uuid = :cocktail
  AND i.uuid = :id
ON CONFLICT ON CONSTRAINT cocktail_ingredient_cocktail_ingredient_key DO NOTHING;
