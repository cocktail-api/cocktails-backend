SELECT uuid AS user_uuid,
       nick AS user_nick
FROM "user"
WHERE nick = :nick;
