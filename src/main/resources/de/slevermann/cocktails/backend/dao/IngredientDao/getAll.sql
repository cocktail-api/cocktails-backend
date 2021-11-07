SELECT i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       it.name       as type_name,
       it.uuid       as type_uuid
FROM ingredient i
         JOIN ingredient_type it on i.type = it.id
ORDER BY i.id
LIMIT :pageSize OFFSET :offset
