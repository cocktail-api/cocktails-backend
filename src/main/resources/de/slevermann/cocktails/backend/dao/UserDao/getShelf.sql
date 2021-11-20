select i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       i.created     as ingredient_created,
       i.modified    as ingredient_modified,
       it.uuid       as type_uuid,
       it.name       as type_name,
       it.created    as type_created,
       it.modified   as type_modified
from ingredient as i
         join ingredient_type it on it.id = i.type
         join user_ingredient ui on i.id = ui.ingredient
         join "user" u on u.id = ui."user"
where u.uuid = :uuid;
