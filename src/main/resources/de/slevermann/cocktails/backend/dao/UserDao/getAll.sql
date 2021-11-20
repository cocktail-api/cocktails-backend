select nick     as user_nick,
       uuid     as user_uuid,
       created  as user_created,
       modified as user_modified
from "user"
order by id
limit :pageSize offset :offset;
