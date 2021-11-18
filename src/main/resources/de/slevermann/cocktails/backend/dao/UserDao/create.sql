INSERT INTO "user" (nick)
VALUES (:nick)
RETURNING uuid AS user_uuid, nick AS user_nick;
