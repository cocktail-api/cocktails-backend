create
extension if not exists "uuid-ossp";

create table cocktail
(
    id          bigserial primary key,
    uuid        uuid unique not null default uuid_generate_v4(),
    name        text        not null,
    description text
);

create table ingredient_type
(
    id   bigserial primary key,
    uuid uuid unique not null default uuid_generate_v4(),
    name text        not null
);

create table ingredient
(
    id          bigserial primary key,
    uuid        uuid unique                            not null default uuid_generate_v4(),
    type        bigint references ingredient_type (id) not null,
    name        text                                   not null,
    description text
);

create table cocktail_ingredient
(
    cocktail   bigint references cocktail (id)   not null,
    ingredient bigint references ingredient (id) not null,
    optional   bool                              not null                                                    default false,
    garnish    bool                              not null check ( not (garnish = true and optional = false)) default false
);

create table "user"
(
    id   bigserial primary key,
    uuid uuid unique not null default uuid_generate_v4(),
    nick text unique not null
);

create table user_ingredient
(
    "user"     bigint references "user" (id)     not null,
    ingredient bigint references ingredient (id) not null
)

