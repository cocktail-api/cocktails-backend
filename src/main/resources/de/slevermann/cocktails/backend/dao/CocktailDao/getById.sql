select name        as cocktail_name,
       uuid        as cocktail_uuid,
       description as cocktail_description,
       created     as cocktail_created,
       modified    as cocktail_modified
from cocktail
where uuid = :uuid;
