package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.util.UUID;

public record DbCocktail(@NonNull UUID id, @NonNull String name, String description) {
}
