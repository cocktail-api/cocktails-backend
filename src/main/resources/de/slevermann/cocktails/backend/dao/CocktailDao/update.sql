UPDATE cocktail
SET name        = :name,
    description = :description
WHERE uuid = :uuid
RETURNING uuid AS cocktail_uuid, name AS cocktail_name, description AS cocktail_description;
