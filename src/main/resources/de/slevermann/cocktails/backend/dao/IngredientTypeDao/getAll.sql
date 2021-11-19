select name as type_name,
       uuid as type_uuid
from ingredient_type
order by id
limit :pageSize offset :offset;
