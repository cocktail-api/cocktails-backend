package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DbCocktailIngredient(@NonNull UUID id,
                                   @NonNull DbIngredientType type,
                                   @NonNull String name,
                                   String description,
                                   Double amount,
                                   DbUnit unit,
                                   boolean garnish,
                                   boolean optional,
                                   OffsetDateTime created,
                                   OffsetDateTime modified) {

    public DbCocktailIngredient(@NonNull final DbIngredient ingredient,
                                final Double amount,
                                final DbUnit unit,
                                boolean garnish,
                                boolean optional,
                                OffsetDateTime created,
                                OffsetDateTime modified) {
        this(ingredient.id(),
                ingredient.type(),
                ingredient.name(),
                ingredient.description(),
                amount,
                unit,
                garnish,
                optional,
                created,
                modified);
    }

    public DbCocktailIngredient(@NonNull final DbIngredient ingredient,
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
                optional,
                null,
                null);
    }

}
