DELETE
FROM user_ingredient
    USING "user" AS u,
        ingredient AS i
WHERE ingredient = i.id
  AND "user" = u.id
  AND i.uuid = :ingredient
  AND u.uuid = :user;
