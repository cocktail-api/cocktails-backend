package de.slevermann.cocktails.backend.model;

import de.slevermann.cocktails.backend.model.db.DbCocktail;
import de.slevermann.cocktails.backend.model.db.DbIngredient;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public record Cocktail(@NonNull UUID id, @NonNull String name, String description,
                       @NonNull List<DbIngredient> dbIngredients) {

    public Cocktail(@NonNull DbCocktail dbCocktail, @NonNull List<DbIngredient> dbIngredients) {
        this(dbCocktail.id(), dbCocktail.name(), dbCocktail.description(), dbIngredients);
    }
}
