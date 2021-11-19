insert into instructions (number, text, cocktail)
select :number, :text, c.id
from cocktail c
where c.uuid = :cocktail
returning number as instruction_number, text as instruction_text;