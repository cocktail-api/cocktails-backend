SELECT name as type_name,
       uuid as type_uuid
FROM ingredient_type
WHERE uuid = :uuid
