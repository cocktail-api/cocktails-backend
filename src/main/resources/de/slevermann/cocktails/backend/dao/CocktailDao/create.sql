insert into cocktail (name, description)
values (:name, :description)
returning uuid as cocktail_uuid, name as cocktail_name, description as cocktail_description, created as cocktail_created, modified as cocktail_modified;
