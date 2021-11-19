select name     as type_name,
       uuid     as type_uuid,
       created  as type_created,
       modified as type_modified
from ingredient_type
order by id
limit :pageSize offset :offset;
