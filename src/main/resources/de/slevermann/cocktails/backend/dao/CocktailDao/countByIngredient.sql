select count(*)
from cocktail c
         join cocktail_ingredient ci on c.id = ci.cocktail
         join ingredient i on ci.ingredient = i.id
where i.uuid = :uuid
