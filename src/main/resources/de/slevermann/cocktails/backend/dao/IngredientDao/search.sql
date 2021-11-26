select i.uuid        as ingredient_uuid,
       i.name        as ingredient_name,
       i.description as ingredient_description,
       i.created     as ingredient_created,
       i.modified    as ingredient_modified,
       it.name       as type_name,
       it.uuid       as type_uuid,
       it.created    as type_created,
       it.modified   as type_modified
from ingredient i
         join ingredient_type it on i.type = it.id
where i.name % :searchTerm
   or i.description ilike :likeSearchTerm
order by similarity(i.name, :searchTerm) desc,
         similarity(i.description, :searchTerm) desc,
         i.name
limit :pageSize offset :offset;
