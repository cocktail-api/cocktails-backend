select count(*)
from ingredient
         join ingredient_type it on it.id = ingredient.type
where it.uuid = :uuid;
