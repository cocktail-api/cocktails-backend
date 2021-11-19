select number     as instruction_number,
       text       as instruction_text,
       i.created  as instruction_created,
       i.modified as instruction_modified
from instructions i
         join cocktail c on c.id = i.cocktail
where c.uuid = :cocktail
order by number;
