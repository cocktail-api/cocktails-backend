SELECT name as type_name,
       uuid as type_uuid
FROM ingredient_type
ORDER BY id
LIMIT :pageSize OFFSET :offset
