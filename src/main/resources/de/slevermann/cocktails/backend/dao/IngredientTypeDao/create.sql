insert into ingredient_type (name)
values (:name)
returning uuid as type_uuid, name as type_name;
