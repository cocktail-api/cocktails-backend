alter table cocktail_ingredient
    drop constraint cocktail_ingredient_cocktail_fkey,
    add constraint cocktail_ingredient_cocktail_fkey
        foreign key (cocktail) references cocktail (id)
            on delete cascade;
