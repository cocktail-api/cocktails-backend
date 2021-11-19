SELECT nick as user_nick,
       uuid as user_uuid
FROM "user"
ORDER BY id
LIMIT :pageSize OFFSET :offset
