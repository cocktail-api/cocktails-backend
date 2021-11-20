alter table instruction
    drop constraint instructions_cocktail_fkey,
    add constraint instruction_cocktail_fkey
        foreign key (cocktail) references cocktail (id)
            on delete cascade;
