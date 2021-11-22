select count(*)
from ingredient i
         join ingredient_type it on i.type = it.id
where it.uuid = :type
