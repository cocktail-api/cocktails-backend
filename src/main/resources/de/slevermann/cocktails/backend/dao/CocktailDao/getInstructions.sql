select number as instruction_number,
       text   as instruction_text
from instructions
         join cocktail c on c.id = instructions.cocktail
where c.uuid = :cocktail
order by number;
