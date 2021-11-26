select c.name        as cocktail_name,
       c.description as cocktail_description,
       c.uuid        as cocktail_uuid,
       c.created     as cocktail_created,
       c.modified    as cocktail_modified
from cocktail c
where c.name % :searchTerm
   or c.description ilike :likeSearchTerm
order by similarity(c.name, :searchTerm) desc,
         similarity(c.description, :searchTerm) desc,
         c.name
limit :pageSize offset :offset;
