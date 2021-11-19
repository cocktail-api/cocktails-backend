package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DbIngredientType(@NonNull UUID id,
                               @NonNull String name,
                               OffsetDateTime created,
                               OffsetDateTime modified) {
    public DbIngredientType(@NonNull UUID id,
                            @NonNull String name) {
        this(id, name, null, null);
    }
}
