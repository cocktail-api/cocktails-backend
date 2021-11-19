insert into cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
select c.id, i.id, :optional, :garnish, :amount, :unit
from cocktail c,
     ingredient i
where c.uuid = :cocktail
  and i.uuid = :id
on conflict on constraint cocktail_ingredient_pkey do update set optional = :optional,
                                                                 garnish  = :garnish,
                                                                 amount   = :amount,
                                                                 unit     = :unit;
