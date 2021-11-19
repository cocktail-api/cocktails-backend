select count(*)
from cocktail_ingredient
         join ingredient i on i.id = cocktail_ingredient.ingredient
where i.uuid = :uuid;
