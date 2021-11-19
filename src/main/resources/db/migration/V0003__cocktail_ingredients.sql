create type unit as enum ('grams', 'milliliters', 'barspoons');

alter table cocktail_ingredient
    add column amount double precision,
    add column unit   unit;

delete from cocktail_ingredient;
delete from cocktail;

insert into cocktail (name)
values ('Daiquiri'),
       ('Dark and Stormy');

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
select c.id, i.id, false, false, 60, 'milliliters'
from cocktail c,
     ingredient i
where c.name = 'Daiquiri' and i.name = 'White rum';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
select c.id, i.id, false, false, 20, 'milliliters'
from cocktail c,
     ingredient i
where c.name = 'Daiquiri' and i.name = 'Simple syrup';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
select c.id, i.id, false, false, 30, 'milliliters'
from cocktail c,
     ingredient i
where c.name = 'Daiquiri' and i.name = 'Lime juice';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, true, true
from cocktail c,
     ingredient i
where c.name = 'Daiquiri' and i.name = 'Lime';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, false, false
from cocktail c,
     ingredient i
where c.name = 'Dark and Stormy' and i.name = 'Dark rum';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, false, false
from cocktail c,
     ingredient i
where c.name = 'Dark and Stormy' and i.name = 'Ginger Beer';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, true, false
from cocktail c,
     ingredient i
where c.name = 'Dark and Stormy' and i.name = 'Simple syrup';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, true, false
from cocktail c,
     ingredient i
where c.name = 'Dark and Stormy' and i.name = 'Lime juice';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, true, true
from cocktail c,
     ingredient i
where c.name = 'Dark and Stormy' and i.name = 'Lime';
