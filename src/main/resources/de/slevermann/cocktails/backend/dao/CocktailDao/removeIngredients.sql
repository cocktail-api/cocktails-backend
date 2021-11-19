delete
from cocktail_ingredient ci
    using cocktail as c, ingredient as i
where c.id = ci.cocktail
  and i.id = ci.ingredient
  and c.uuid = :cocktail
  and i.uuid = :ingredient;
