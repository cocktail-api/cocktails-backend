insert into "user" (nick)
values (:nick)
returning uuid as user_uuid, nick as user_nick, created as user_created, modified as user_modified;
