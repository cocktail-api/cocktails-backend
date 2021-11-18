INSERT INTO "user" DEFAULT
VALUES
RETURNING uuid AS user_uuid, nick AS user_nick;
