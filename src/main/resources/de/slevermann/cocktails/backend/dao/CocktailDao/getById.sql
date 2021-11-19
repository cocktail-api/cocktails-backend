select name        as cocktail_name,
       uuid        as cocktail_uuid,
       description as cocktail_description
from cocktail
where uuid = :uuid;
