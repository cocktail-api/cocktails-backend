SELECT name        as cocktail_name,
       uuid        as cocktail_uuid,
       description as cocktail_description
FROM cocktail
WHERE uuid = :uuid
