select uuid     as user_uuid,
       nick     as user_nick,
       created  as user_created,
       modified as user_modified
from "user"
where nick = :nick;
