with i as (update ingredient
    set name = :name,
        description = :description,
        type = (select id from ingredient_type it where it.uuid = :type),
        modified = :now
    where uuid = :uuid
    returning uuid, name, description, type, created, modified)
select i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       i.created     as ingredient_created,
       i.modified    as ingredient_modified,
       it.name       as type_name,
       it.uuid       as type_uuid,
       it.created    as type_created,
       it.modified   as type_modified
from i
         join ingredient_type it on i.type = it.id;
