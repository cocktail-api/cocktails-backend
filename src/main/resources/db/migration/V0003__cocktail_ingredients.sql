create type unit as enum ('grams', 'milliliters', 'barspoons');

alter table cocktail_ingredient
    add column amount double precision,
    add column unit   unit;

DELETE FROM cocktail_ingredient;
DELETE FROM cocktail;

INSERT INTO cocktail (name)
VALUES ('Daiquiri'),
       ('Dark and Stormy');

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
SELECT c.id, i.id, false, false, 60, 'milliliters'
FROM cocktail c,
     ingredient i
WHERE c.name = 'Daiquiri' AND i.name = 'White rum';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
SELECT c.id, i.id, false, false, 20, 'milliliters'
FROM cocktail c,
     ingredient i
WHERE c.name = 'Daiquiri' AND i.name = 'Simple syrup';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish, amount, unit)
SELECT c.id, i.id, false, false, 30, 'milliliters'
FROM cocktail c,
     ingredient i
WHERE c.name = 'Daiquiri' AND i.name = 'Lime juice';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, true, true
FROM cocktail c,
     ingredient i
WHERE c.name = 'Daiquiri' AND i.name = 'Lime';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, false, false
FROM cocktail c,
     ingredient i
WHERE c.name = 'Dark and Stormy' AND i.name = 'Dark rum';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, false, false
FROM cocktail c,
     ingredient i
WHERE c.name = 'Dark and Stormy' AND i.name = 'Ginger Beer';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, true, false
FROM cocktail c,
     ingredient i
WHERE c.name = 'Dark and Stormy' AND i.name = 'Simple syrup';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, true, false
FROM cocktail c,
     ingredient i
WHERE c.name = 'Dark and Stormy' AND i.name = 'Lime juice';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, true, true
FROM cocktail c,
     ingredient i
WHERE c.name = 'Dark and Stormy' AND i.name = 'Lime';
