update "user"
set nick = :nick
where uuid = :uuid
returning uuid as user_uuid, nick as user_nick;
