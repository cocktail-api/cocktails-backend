delete
from user_ingredient
    using "user" as u,
        ingredient as i
where ingredient = i.id
  and "user" = u.id
  and i.uuid = :ingredient
  and u.uuid = :user;
