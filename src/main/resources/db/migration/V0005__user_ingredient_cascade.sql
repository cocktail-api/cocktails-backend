alter table user_ingredient
    drop constraint user_ingredient_user_fkey,
    add constraint user_ingredient_user_fkey
        foreign key ("user") references "user" (id)
            on delete cascade;
