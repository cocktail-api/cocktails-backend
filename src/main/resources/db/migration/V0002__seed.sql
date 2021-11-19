insert into ingredient_type (name)
values ('Strong alcohol'),
       ('Soft alcohol'),
       ('Soft drink'),
       ('Fruit juice'),
       ('Ice'),
       ('Fruit'),
       ('Syrup'),
       ('Other');

insert into ingredient (type, name, description)
select it.id, 'Bourbon', 'An american whiskey made from corn and other grain'
from ingredient_type it
where it.name = 'Strong alcohol';

insert into ingredient (type, name, description)
select it.id, 'White rum', 'A white liquor distilled from sugarcane'
from ingredient_type it
where it.name = 'Strong alcohol';

insert into ingredient (type, name, description)
select it.id, 'Dark rum', 'An aged liquor distilled from sugarcane'
from ingredient_type it
where it.name = 'Strong alcohol';

insert into ingredient (type, name, description)
select it.id, 'Lime juice', 'Juice. From limes. Big surprise.'
from ingredient_type it
where it.name = 'Fruit juice';

insert into ingredient (type, name, description)
select it.id, 'Lime', 'A green, sour citrus'
from ingredient_type it
where it.name = 'Fruit';

insert into ingredient (type, name, description)
select it.id, 'Ice cubes', 'Ice, in form of a cube.'
from ingredient_type it
where it.name = 'Ice';

insert into ingredient (type, name, description)
select it.id, 'Ginger Beer', 'A spicy ginger drink.'
from ingredient_type it
where it.name = 'Soft drink';

insert into ingredient (type, name, description)
select it.id, 'Simple syrup', 'Syrup made from sugar.'
from ingredient_type it
where it.name = 'Syrup';

insert into cocktail (name)
values ('Daiquiri'),
       ('Dark and Stormy');

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, false, false
from cocktail c,
     ingredient i
where c.name = 'Daiquiri' and i.name = 'White rum';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, false, false
from cocktail c,
     ingredient i
where c.name = 'Daiquiri' and i.name = 'Simple syrup';

insert into cocktail_ingredient (cocktail, ingredient, optional, garnish)
select c.id, i.id, false, false
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
where c.name = 'Dark and Stormy' AND i.name = 'Ginger Beer';

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
