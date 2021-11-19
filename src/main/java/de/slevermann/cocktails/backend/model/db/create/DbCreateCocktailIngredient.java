package de.slevermann.cocktails.backend.model.db.create;

import de.slevermann.cocktails.backend.model.db.DbIngredient;
import de.slevermann.cocktails.backend.model.db.DbIngredientType;
import de.slevermann.cocktails.backend.model.db.DbUnit;
import lombok.NonNull;

import java.util.UUID;

public record DbCreateCocktailIngredient(@NonNull UUID id,
                                         @NonNull DbIngredientType type,
                                         @NonNull String name,
                                         String description,
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
                ingredient.type(),
                ingredient.name(),
                ingredient.description(),
                amount,
                unit,
                garnish,
                optional);
    }

}
