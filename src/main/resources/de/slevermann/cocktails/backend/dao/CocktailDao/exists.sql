select exists(
               select * from cocktail where uuid = :uuid
           );
