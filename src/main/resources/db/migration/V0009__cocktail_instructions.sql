create table instructions
(
    number   int                             not null,
    text     text                            not null,
    cocktail bigint references cocktail (id) not null,
    primary key (number, cocktail)
)
