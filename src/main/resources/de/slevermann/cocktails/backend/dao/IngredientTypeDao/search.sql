select name     as type_name,
       uuid     as type_uuid,
       created  as type_created,
       modified as type_modified
from ingredient_type
where name % :searchTerm
order by similarity(name, :searchTerm) desc,
         name
limit :pageSize offset :offset;
