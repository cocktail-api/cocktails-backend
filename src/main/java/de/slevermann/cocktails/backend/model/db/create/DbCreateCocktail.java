package de.slevermann.cocktails.backend.model.db.create;

import lombok.NonNull;

public record DbCreateCocktail(@NonNull String name,
                               String description) {
}
