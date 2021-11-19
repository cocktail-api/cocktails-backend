with i as (insert into ingredient (name, description, type)
    select :name,
           :description,
           (select it.id from ingredient_type it where it.uuid = :type)
    returning uuid, name, description, type
)
select i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       it.name       as type_name,
       it.uuid       as type_uuid
from i
         join ingredient_type it on i.type = it.id;
