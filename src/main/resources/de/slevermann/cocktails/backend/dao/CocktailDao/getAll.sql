SELECT c.name        as cocktail_name,
       c.description as cocktail_description,
       c.uuid        as cocktail_uuid
FROM cocktail c
ORDER BY c.id
LIMIT :pageSize OFFSET :offset
