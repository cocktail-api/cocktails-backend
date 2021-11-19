insert into user_ingredient ("user", ingredient)
select u.id, i.id
from "user" u,
     ingredient i
where u.uuid = :user
  and i.uuid = :ingredient
on conflict on constraint user_ingredient_user_ingredient_key do nothing;
