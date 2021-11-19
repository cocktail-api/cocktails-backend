update ingredient_type
set name     = :name,
    modified = :now
where uuid = :uuid
returning uuid as type_uuid, name as type_name, created as type_created, modified as type_modified;
