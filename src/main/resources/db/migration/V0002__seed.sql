INSERT INTO ingredient_type (name)
VALUES ('Strong alcohol'),
       ('Soft alcohol'),
       ('Soft drink'),
       ('Fruit juice'),
       ('Ice'),
       ('Fruit'),
       ('Syrup'),
       ('Other');

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Bourbon', 'An american whiskey made from corn and other grain'
FROM ingredient_type it
WHERE it.name = 'Strong alcohol';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'White rum', 'A white liquor distilled from sugarcane'
FROM ingredient_type it
WHERE it.name = 'Strong alcohol';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Dark rum', 'An aged liquor distilled from sugarcane'
FROM ingredient_type it
WHERE it.name = 'Strong alcohol';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Lime juice', 'Juice. From limes. Big surprise.'
FROM ingredient_type it
WHERE it.name = 'Fruit juice';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Lime', 'A green, sour citrus'
FROM ingredient_type it
WHERE it.name = 'Fruit';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Ice cubes', 'Ice, in form of a cube.'
FROM ingredient_type it
WHERE it.name = 'Ice';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Ginger Beer', 'A spicy ginger drink.'
FROM ingredient_type it
WHERE it.name = 'Soft drink';

INSERT INTO ingredient (type, name, description)
SELECT it.id, 'Simple syrup', 'Syrup made from sugar.'
FROM ingredient_type it
WHERE it.name = 'Syrup';

INSERT INTO cocktail (name)
VALUES ('Daiquiri'),
       ('Dark and Stormy');

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, false, false
FROM cocktail c,
     ingredient i
WHERE c.name = 'Daiquiri' AND i.name = 'White rum';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, false, false
FROM cocktail c,
     ingredient i
WHERE c.name = 'Daiquiri' AND i.name = 'Simple syrup';

INSERT INTO cocktail_ingredient (cocktail, ingredient, optional, garnish)
SELECT c.id, i.id, false, false
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
