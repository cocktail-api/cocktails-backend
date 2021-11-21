package de.slevermann.cocktails.backend.model.db.create;

import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import lombok.NonNull;

import java.util.UUID;

public record DbCreateCocktailIngredient(@NonNull UUID id,
                                         Double amount,
                                         DbUnit unit,
                                         boolean garnish,
                                         boolean optional) {
    public DbCreateCocktailIngredient(@NonNull final DbIngredient ingredient,
                                final Double amount,
                                final DbUnit unit,
                                boolean garnish,
                                boolean optional) {
        this(ingredient.id(),
                amount,
                unit,
                garnish,
                optional);
    }

}
