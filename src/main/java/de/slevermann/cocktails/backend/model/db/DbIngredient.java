package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DbIngredient(@NonNull UUID id,
                           @NonNull DbIngredientType type,
                           @NonNull String name,
                           String description,
                           OffsetDateTime created,
                           OffsetDateTime modified) {
    public DbIngredient(@NonNull UUID id,
                        @NonNull DbIngredientType type,
                        @NonNull String name,
                        String description) {
        this(id, type, name, description, null, null);
    }
}
