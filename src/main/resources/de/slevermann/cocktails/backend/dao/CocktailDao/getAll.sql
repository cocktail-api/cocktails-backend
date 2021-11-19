select c.name        as cocktail_name,
       c.description as cocktail_description,
       c.uuid        as cocktail_uuid
from cocktail c
order by c.id
limit :pageSize offset :offset;
