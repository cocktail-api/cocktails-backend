package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.util.UUID;

public record DbIngredient(@NonNull UUID id,
                           @NonNull DbIngredientType type,
                           @NonNull String name,
                           String description) {
}
