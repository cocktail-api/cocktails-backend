create extension if not exists "pg_trgm";

create index cocktail_trgm on cocktail using gin ((name || ' ' || coalesce(description, '')) gin_trgm_ops);
create index ingredient_trgm on ingredient using gin ((name || ' ' || coalesce(description, '')) gin_trgm_ops);
create index ingredient_type_trgm on ingredient_type using gin (name gin_trgm_ops);
