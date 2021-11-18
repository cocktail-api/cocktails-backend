UPDATE "user"
SET nick = :nick
WHERE uuid = :uuid
RETURNING uuid as user_uuid, nick as user_nick;
