update "user"
set nick     = :nick,
    modified = :now
where uuid = :uuid
returning uuid as user_uuid, nick as user_nick, created as user_created, modified as user_modified;
