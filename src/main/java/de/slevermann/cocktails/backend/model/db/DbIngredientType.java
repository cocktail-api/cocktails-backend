package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.util.UUID;

public record DbIngredientType(@NonNull UUID id, @NonNull String name) {
}
