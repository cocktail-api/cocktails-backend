update cocktail
set name        = :name,
    description = :description,
    modified    = :now
where uuid = :uuid
returning uuid as cocktail_uuid, name as cocktail_name, description as cocktail_description, created as cocktail_created, modified as cocktail_modified;
