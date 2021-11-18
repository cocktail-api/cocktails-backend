package de.slevermann.cocktails.backend.model.db;

import lombok.NonNull;

import java.util.UUID;

public record DbUser(@NonNull UUID id,
                     String nick) {
}
