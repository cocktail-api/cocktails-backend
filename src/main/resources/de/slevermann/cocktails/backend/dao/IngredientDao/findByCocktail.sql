select i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       i.created     as ingredient_created,
       i.modified    as ingredient_modified,
       it.name       as type_name,
       it.uuid       as type_uuid,
       it.created    as type_created,
       it.modified   as type_modified,
       ci.unit       as unit,
       ci.amount     as amount
from ingredient i
         join ingredient_type it on i.type = it.id
         join cocktail_ingredient ci on i.id = ci.ingredient
         join cocktail c on ci.cocktail = c.id
where c.uuid = :uuid;
