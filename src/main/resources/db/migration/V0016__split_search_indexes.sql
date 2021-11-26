drop index cocktail_trgm;
drop index ingredient_trgm;
drop index ingredient_type_trgm;

create index cocktail_name_trgm on cocktail using gin (name gin_trgm_ops);
create index ingredient_name_trgm on ingredient using gin (name gin_trgm_ops);
create index ingredient_type_name_trgm on ingredient_type using gin (name gin_trgm_ops);

create index cocktail_description_trgm on cocktail using gin (description gin_trgm_ops);
create index ingredient_description_trgm on ingredient using gin (description gin_trgm_ops);
