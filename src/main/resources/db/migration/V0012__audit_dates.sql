alter table cocktail
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
alter table cocktail_ingredient
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
alter table ingredient
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
alter table ingredient_type
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
alter table instructions
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
alter table "user"
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
alter table user_ingredient
    add column created  timestamp with time zone not null default now(),
    add column modified timestamp with time zone not null default now();
