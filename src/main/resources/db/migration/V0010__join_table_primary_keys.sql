alter table cocktail_ingredient
    drop constraint cocktail_ingredient_cocktail_ingredient_key,
    add primary key (cocktail, ingredient);
alter table user_ingredient
    drop constraint user_ingredient_user_ingredient_key,
    add primary key ("user", ingredient);
