alter table cocktail_ingredient
    add constraint cocktail_ingredient_cocktail_ingredient_key unique (cocktail, ingredient)
