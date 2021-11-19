select name as type_name,
       uuid as type_uuid
from ingredient_type
where uuid = :uuid;
