update cocktail
set name        = :name,
    description = :description
where uuid = :uuid
returning uuid as cocktail_uuid, name as cocktail_name, description as cocktail_description;
