select c.name        as cocktail_name,
       c.description as cocktail_description,
       c.uuid        as cocktail_uuid,
       c.created     as cocktail_created,
       c.modified    as cocktail_modified
from cocktail c
order by c.id
limit :pageSize offset :offset;
