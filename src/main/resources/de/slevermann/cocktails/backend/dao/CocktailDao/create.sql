INSERT INTO cocktail (name, description)
VALUES (:name, :description)
RETURNING uuid AS cocktail_uuid, name AS cocktail_name, description AS cocktail_description;
