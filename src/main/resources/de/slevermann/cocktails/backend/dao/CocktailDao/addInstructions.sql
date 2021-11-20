insert into instruction (number, text, cocktail)
select :number, :text, c.id
from cocktail c
where c.uuid = :cocktail
returning number as instruction_number, text as instruction_text, created as instruction_created, modified as instruction_modified;
