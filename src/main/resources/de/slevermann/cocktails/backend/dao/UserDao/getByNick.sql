select uuid as user_uuid,
       nick as user_nick
from "user"
where nick = :nick;
