delete
from instructions i
    using cocktail c
where c.id = i.cocktail
  and c.uuid = :cocktail
