INSERT INTO user_ingredient ("user", ingredient)
SELECT u.id, i.id
FROM "user" u,
     ingredient i
WHERE u.uuid = :user
  AND i.uuid = :ingredient
ON CONFLICT ON CONSTRAINT user_ingredient_user_ingredient_key DO NOTHING;
