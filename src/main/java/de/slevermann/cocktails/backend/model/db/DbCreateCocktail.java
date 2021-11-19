package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

public record DbCreateCocktail(@NonNull String name,
                               String description) {
}
