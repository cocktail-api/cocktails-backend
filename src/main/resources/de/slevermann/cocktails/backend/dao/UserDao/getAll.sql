select nick as user_nick,
       uuid as user_uuid
from "user"
order by id
limit :pageSize offset :offset;
