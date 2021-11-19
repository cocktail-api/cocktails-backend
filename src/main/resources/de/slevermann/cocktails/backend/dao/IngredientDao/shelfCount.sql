select count(*)
from user_ingredient
         join ingredient i on i.id = user_ingredient.ingredient
where i.uuid = :uuid;
