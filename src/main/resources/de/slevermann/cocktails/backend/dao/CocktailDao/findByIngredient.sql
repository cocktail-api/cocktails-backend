select c.name        as cocktail_name,
       c.description as cocktail_description,
       c.uuid        as cocktail_uuid,
       c.created     as cocktail_created,
       c.modified    as cocktail_modified
from cocktail c
         join cocktail_ingredient ci on c.id = ci.cocktail
         join ingredient i on ci.ingredient = i.id
where i.uuid = :uuid
order by c.id
limit :pageSize offset :offset;
