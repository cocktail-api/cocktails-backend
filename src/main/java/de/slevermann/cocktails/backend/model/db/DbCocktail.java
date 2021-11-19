package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DbCocktail(@NonNull UUID id,
                         @NonNull String name,
                         String description,
                         OffsetDateTime created,
                         OffsetDateTime modified) {
    public DbCocktail(@NonNull UUID id,
                      @NonNull String name,
                      String description) {
        this(id, name, description, null, null);
    }
}
