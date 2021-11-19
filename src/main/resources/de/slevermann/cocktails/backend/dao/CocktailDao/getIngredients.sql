SELECT i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       it.name       as type_name,
       it.uuid       as type_uuid,
       ci.unit       as unit,
       ci.amount     as amount,
       ci.garnish    as garnish,
       ci.optional   as optional
FROM ingredient i
         JOIN ingredient_type it on i.type = it.id
         JOIN cocktail_ingredient ci on i.id = ci.ingredient
         JOIN cocktail c on ci.cocktail = c.id
WHERE c.uuid = :uuid
