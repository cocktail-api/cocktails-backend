alter table user_ingredient
    add constraint user_ingredient_user_ingredient_key unique ("user", ingredient)
