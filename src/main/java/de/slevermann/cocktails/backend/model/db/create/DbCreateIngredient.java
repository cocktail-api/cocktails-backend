package de.slevermann.cocktails.backend.model.db.create;

import lombok.NonNull;

import java.util.UUID;

public record DbCreateIngredient(@NonNull UUID type,
                                 @NonNull String name,
                                 String description) {
}
