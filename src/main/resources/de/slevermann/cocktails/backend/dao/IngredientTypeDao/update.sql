update ingredient_type
set name = :name
where uuid = :uuid
returning uuid as type_uuid, name as type_name;
